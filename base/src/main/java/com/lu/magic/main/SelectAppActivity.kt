package com.lu.magic.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.collection.LruCache
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lu.magic.ModuleProviders
import com.lu.magic.base.R
import com.lu.magic.base.databinding.LayoutSelectAppBinding
import com.lu.magic.bean.AMapConfig
import com.lu.magic.bean.BaseConfig
import com.lu.magic.bridge.BridgeConstant
import com.lu.magic.config.ConfigUtil
import com.lu.magic.config.ModuleId
import com.lu.magic.main.AppRouter.routeDetailConfigPage
import com.lu.magic.main.AppRouter.routeWitchApp
import com.lu.magic.store.ItemModel
import com.lu.magic.ui.BaseActivity
import com.lu.magic.ui.recycler.MultiAdapter
import com.lu.magic.ui.recycler.MultiViewHolder
import com.lu.magic.ui.recycler.SimpleItemType
import com.lu.magic.util.GsonUtil
import com.lu.magic.util.PackageUtil.Companion.getInstallPackageInfoList
import com.lu.magic.util.PackageUtil.Companion.isDebugApp
import com.lu.magic.util.PackageUtil.Companion.isSystemApp
import com.lu.magic.util.SingleClassStoreUtil
import com.lu.magic.util.ToastUtil
import com.lu.magic.util.load.LoaderCacheUtil
import com.lu.magic.util.load.LoaderCacheUtil.ObjectLoader
import com.lu.magic.util.log.LogUtil
import com.lu.magic.util.thread.WorkerUtil
import org.json.JSONObject
import java.util.Collections
import java.util.Locale


class SelectAppActivity : BaseActivity() {
    private var binding: LayoutSelectAppBinding? = null
    private var routeItem: ItemModel? = null
    private var appListAdapter: MultiAdapter<AppListModel>? = null
    private val installAppModelMap = HashMap<String, AppListModel>()
    private var sortActionId = R.id.sort_package_name_A_Z
    private var filterActionId = R.id.filter_hide_system_app
    private var mClickItemData: AppListModel? = null

    companion object {
        @JvmStatic
        fun start(context: Context, item: ItemModel) {
            if (item.module.moduleId.isEmpty()) {
                return
            }
            val intent = Intent(context, SelectAppActivity::class.java)
            intent.putExtra(Constant.KEY_ROUTE_ITEM, item)
            context.startActivity(intent)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSelectAppBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val route = intent.getSerializableExtra(Constant.KEY_ROUTE_ITEM)
        if (route == null) {
            finish()
            return
        }
        routeItem = route as ItemModel?
        if (routeItem!!.module.moduleId.isEmpty()) {
            return
        }
        initToolBar()
        initViewForAppList()
        initViewForSearch()
        loadInstallInfoList()
    }

    private fun initToolBar() {
        val toolbar = binding!!.appBarLayout.toolbar
        toolbar.title = routeItem!!.module.title
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        toolbar.setNavigationOnClickListener { v: View? -> finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        appListAdapter!!.removeDataObserver()
        SingleClassStoreUtil.remove(AppListModel::class.java)
        SingleClassStoreUtil.remove(routeItem!!.javaClass)
    }

    private fun initViewForSearch() {
        val editFrame = binding!!.searchView.findViewById<View>(androidx.appcompat.R.id.search_edit_frame)
        binding!!.searchView.setOnSearchClickListener { v: View? ->
            val lp = binding!!.searchView.layoutParams as ConstraintLayout.LayoutParams
            lp.startToEnd = binding!!.svAppListFilter.id
            binding!!.searchView.layoutParams = lp
        }
        binding!!.searchView.setOnCloseListener {
            val lp = binding!!.searchView.layoutParams as ConstraintLayout.LayoutParams
            lp.startToEnd = ConstraintLayout.LayoutParams.UNSET
            binding!!.searchView.layoutParams = lp
            false
        }
        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                queryAndShowAppListView(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                queryAndShowAppListView(newText)
                return false
            }
        })
        binding!!.svAppListFilter.setOnMenuShowListener { popupMenu: PopupMenu ->
            val filterMenuItem = popupMenu.menu.findItem(filterActionId)
            filterMenuItem?.setChecked(true)
            val sortMenuItem = popupMenu.menu.findItem(sortActionId)
            sortMenuItem?.setChecked(true)
        }
        binding!!.svAppListFilter.setOnMenuItemClickListener { item: MenuItem ->
            item.setChecked(true)
            val groupId = item.groupId
            if (groupId == R.id.group_sort) {
                sortActionId = item.itemId
            } else if (groupId == R.id.group_filter) {
                filterActionId = item.itemId
            }
            updateInstallInfoList()
            false
        }
    }

    private fun initViewForAppList() {
        appListAdapter = object : MultiAdapter<AppListModel>() {}.setDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (!isFinishing || isDestroyed) {
                    return
                }
                binding!!.tvAppCount.text = "数量：" + appListAdapter!!.getData().size + ""
            }
        }).addItemType(object : SimpleItemType<AppListModel>() {
            override fun createViewHolder(adapter: MultiAdapter<AppListModel>, parent: ViewGroup, viewType: Int): MultiViewHolder<AppListModel> {
                val v = LayoutInflater.from(getContext()).inflate(R.layout.item_enable_list, parent, false)
                return ItemViewHolder(v)
            }
        })
        val rvAppList = binding!!.rvAppList
        rvAppList.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        rvAppList.adapter = appListAdapter
    }

    private fun updateInstallInfoList() {
        WorkerUtil.loadSingle {
            val filterApp = filterApp(installAppModelMap)
            sortApp(filterApp)
            filterApp
        }.intoMain { appListModels: List<AppListModel>? ->
            appListAdapter!!.updateDataAt(appListModels!!)
            binding!!.rvAppList.smoothScrollToPosition(0)
        }
    }

    private fun queryAndShowAppListView(keyWord: String) {
        val filterApp = filterApp(installAppModelMap, keyWord)
        sortApp(filterApp)
        appListAdapter!!.updateDataAt(filterApp)
    }

    private fun filterApp(appInfoMap: Map<String, AppListModel>, keyWord: String = ""): List<AppListModel> {
        val entrySet = appInfoMap.entries
        val filterId = filterActionId
        val appListModels: MutableList<AppListModel> = ArrayList()
        val it = entrySet.iterator()
        val upKeyWord = keyWord.uppercase(Locale.getDefault())
        while (it.hasNext()) {
            val (_, model) = it.next()
            val pkgInfo = model.packageInfo

            //去掉不包含关键字的包,忽略大小写
            if (!TextUtils.isEmpty(upKeyWord)
                && !model.name.uppercase(Locale.getDefault()).contains(upKeyWord)
                && !model.packageName.uppercase(Locale.getDefault()).contains(upKeyWord)
            ) {
                continue
            }
            if (filterId == R.id.filter_only_show_system_app) {
                if (isSystemApp(pkgInfo)) {
                    appListModels.add(model)
                }
            } else if (filterId == R.id.filter_hide_system_app) {
                if (!isSystemApp(pkgInfo)) {
                    appListModels.add(model)
                }
            } else if (filterId == R.id.filter_only_show_debug_app) {
                if (isDebugApp(pkgInfo)) {
                    appListModels.add(model)
                }
            } else {
                appListModels.add(model)
            }
        }
        return appListModels
    }

    private fun sortApp(appListModels: List<AppListModel>) {
        val sortId = sortActionId
        Collections.sort<AppListModel>(appListModels) { o1: AppListModel, o2: AppListModel ->
            //打开开关的在前，所以index小
            if (o1.enable && !o2.enable) {
                return@sort -1
            }
            if (o2.enable && !o1.enable) {
                return@sort 1
            }
            if (sortId == R.id.sort_app_name_A_Z) { //都是enable或都不是
                return@sort o1.name.compareTo(o2.name)
            } else if (sortId == R.id.sort_app_name_Z_A) {
                return@sort o2.name.compareTo(o1.name)
            } else if (sortId == R.id.sort_package_name_A_Z) {
                return@sort o1.packageName.compareTo(o2.packageName)
            } else if (sortId == R.id.sort_package_name_Z_A) {
                return@sort o2.packageName.compareTo(o1.packageName)
            }
            0
        }
    }

    private fun loadInstallInfoList() {
        WorkerUtil.loadSingle {
            val appModelMap = HashMap<String, AppListModel>()
            val enableMap = ConfigUtil.getSheet(routeItem!!.module.moduleId, BaseConfig::class.java)
            val installInfoList: List<PackageInfo> = getInstallPackageInfoList(getContext())
            for (packageInfo in installInfoList) {
                val appInfo = packageInfo.applicationInfo
                //                耗时操作，放到其他线程中，以免ui显示等待较长，这里只拿基本的信息
//                String appName = appInfo.loadLabel(getContext().getPackageManager()) + "";
                val appName = if (appInfo.name == null) "" else appInfo.name
                val packageName = if (appInfo.packageName == null) "" else appInfo.packageName
                val configBean = enableMap[packageName]
                val enable = configBean != null && configBean.isEnable
                val model = AppListModel(packageInfo, appName, packageName, null, enable)
                appModelMap[packageName] = model
            }
            installAppModelMap.putAll(appModelMap)
            val filterList = filterApp(appModelMap)
            sortApp(filterList)
            filterList
        }.intoMain { filterList: List<AppListModel>? ->
            appListAdapter!!.updateDataAt(filterList!!)
            binding!!.rvAppList.smoothScrollToPosition(0)
            updateAppNameAsync()
        }
    }

    private fun updateAppNameAsync() {
        val local = installAppModelMap
        WorkerUtil.executeSingle {
            val entrySet: Set<Map.Entry<String, AppListModel>> = local.entries
            val it = entrySet.iterator()
            val pm = getContext().packageManager
            while (it.hasNext()) {
                val (_, model) = it.next()
                val appInfo = model.packageInfo.applicationInfo
                val appName = appInfo.loadLabel(pm).toString() + ""
                model.name = appName
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.select_app, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private inner class ItemViewHolder(itemView: View) : MultiViewHolder<AppListModel>(itemView) {
        private val appListModelLoader: ObjectLoader<AppListModel?>
        private val tvPackageName: TextView
        private val tvAppName: TextView
        private val ivAppIcon: ImageView
        private val sbEnableItem: SwitchCompat

        init {
            appListModelLoader = LoaderCacheUtil.createObjectLoader(LruCache(100))
            ivAppIcon = itemView.findViewById(R.id.ivHeadIcon)
            tvAppName = itemView.findViewById(R.id.tvBodyTitle)
            tvPackageName = itemView.findViewById(R.id.tvBodySubTitle)
            sbEnableItem = itemView.findViewById(R.id.sbEnableItem)
            itemView.setOnClickListener { v: View? ->
                val clickPosition = layoutPosition
                val itemData = appListAdapter!!.getItem(clickPosition)
                mClickItemData = itemData

                if (routeItem!!.module.moduleId == ModuleId.AMAP_LOCATION) {
                    //高德地图
                    routeWitchApp(this@SelectAppActivity, routeItem!!, itemData!!)
                    return@setOnClickListener
                }
                val module = ModuleProviders.get(routeItem!!.module.moduleId)
                if (module == null || module.detailFragmentFactory == null) {
                    return@setOnClickListener
                } else {
                    routeDetailConfigPage(this@SelectAppActivity, routeItem!!, itemData)
                }
            }
            sbEnableItem.setOnClickListener { v: View ->
                val compoundButton = v as CompoundButton
                val check = compoundButton.isChecked
                val clickPosition = layoutPosition
                val itemData = appListAdapter!!.getItem(clickPosition)
                itemData!!.enable = check
                val packageName = itemData.packageName
                //打开指定表的配置
                ConfigUtil.enableCell(routeItem!!.module.moduleId, packageName, check)
            }
        }

        override fun onBindView(adapter: MultiAdapter<AppListModel>, itemModel: AppListModel, position: Int) {
            tvAppName.text = itemModel.name
            tvPackageName.text = itemModel.packageName
            sbEnableItem.isChecked = itemModel.enable

            //每次加载前都设置tag
            ivAppIcon.tag = position
            appListModelLoader.loadIO(itemModel.packageName) {
                val model = installAppModelMap[itemModel.packageName]
                val packageInfo = model!!.packageInfo
                val appInfo = packageInfo.applicationInfo
                val pm = getContext().packageManager
                val drawable = appInfo.loadIcon(pm)
                if (TextUtils.isEmpty(model.name) || model.name == appInfo.name) {
                    val label = appInfo.loadLabel(pm).toString() + ""
                    model.name = label
                }
                model.icon = drawable
                model
            }.intoMain { appListModel: AppListModel? ->
                val tag = ivAppIcon.tag
                if (Integer.valueOf(position) != tag) {
                    //tag不相等，说明异步加载出的图片不是要展示的。
                    //加载需要时间，划view到不可见后，被view复用时，会重新走到设置tag
                    //忽略掉，防止错位、闪现
                    return@intoMain
                }
                //                LogUtil.d(">>>position", position);
                tvAppName.text = appListModel!!.name
                ivAppIcon.setImageDrawable(appListModel.icon)
                tvPackageName.text = appListModel.packageName
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LogUtil.i("requestCode:", requestCode, "resultCode:", resultCode, "data:", data)
        if (data != null && requestCode == BridgeConstant.CODE_ACTIVITY_REQUEST_CODE) {
            val jsonText = data.getStringExtra(BridgeConstant.CODE_INTENT_RESULT_DATA)
            LogUtil.i("result data:", jsonText)
            handleActivityResult(jsonText)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleActivityResult(jsonText: String?) {
        if (routeItem == null) {
            return
        }
        val json = JSONObject(jsonText)
        val moduleId = routeItem!!.module.moduleId

        if (moduleId == ModuleId.AMAP_LOCATION) {
            var lat = json.optDouble("lat", 0.0)
            var lng = json.optDouble("lng", 0.0)
            if (lat == 0.0 && lng == 0.0) {
                ToastUtil.show("位置获取失败")
            }
            if (mClickItemData != null) {
                val configPackage = mClickItemData!!.packageName

                var aMapConfig = ConfigUtil.getAMapConfig(configPackage)
                if (aMapConfig == null) {
                    aMapConfig = AMapConfig()
                }
                aMapConfig.setLng(lng)
                aMapConfig.setLat(lat)
                ConfigUtil.setAMapConfig(configPackage, aMapConfig)
                LogUtil.d("位置更新：", GsonUtil.toJson(ConfigUtil.getAMapConfig(configPackage)))
            }
        }

    }


}
