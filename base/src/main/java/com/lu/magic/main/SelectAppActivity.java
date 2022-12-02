package com.lu.magic.main;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.LruCache;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kyleduo.switchbutton.SwitchButton;
import com.lu.magic.IModuleFace;
import com.lu.magic.ModuleProviders;
import com.lu.magic.base.databinding.LayoutSelectAppBinding;
import com.lu.magic.bean.BaseConfig;
import com.lu.magic.base.R;

import com.lu.magic.store.ItemModel;
import com.lu.magic.ui.BaseActivity;
import com.lu.magic.ui.recycler.MultiAdapter;
import com.lu.magic.ui.recycler.MultiViewHolder;
import com.lu.magic.ui.recycler.SimpleItemType;
import com.lu.magic.util.PackageUtil;
import com.lu.magic.util.SingleStoreUtil;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.util.load.LoaderCacheUtil;
import com.lu.magic.util.thread.WorkerUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectAppActivity extends BaseActivity {
    private LayoutSelectAppBinding binding;
    private ItemModel routeItem;
    private MultiAdapter<AppListModel> appListAdapter;
    private HashMap<String, AppListModel> installAppModelMap = new HashMap<>();
    private int sortActionId = R.id.sort_package_name_A_Z;
    private int filterActionId = R.id.filter_hide_system_app;

    public static void start(Context context, ItemModel item) {
        if (item.getModule().getModuleId().isEmpty()) {
            return;
        }
        Intent intent = new Intent(context, SelectAppActivity.class);
        intent.putExtra(Constant.KEY_ROUTE_ITEM, item);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutSelectAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Serializable route = getIntent().getSerializableExtra(Constant.KEY_ROUTE_ITEM);
        if (route == null) {
            finish();
            return;
        }
        routeItem = (ItemModel) route;
        if (routeItem.getModule().getModuleId().isEmpty()) {
            return;
        }
        initToolBar();

        initViewForAppList();
        initViewForSearch();
        loadInstallInfoList();
    }


    private void initToolBar() {
        Toolbar toolbar = binding.appBarLayout.toolbar;
        toolbar.setTitle(routeItem.getModule().getTitle());
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(toolbar);
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        toolbar.setNavigationOnClickListener((v) -> {
            finish();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appListAdapter.removeDataObserver();
        SingleStoreUtil.remove(AppListModel.class);
        SingleStoreUtil.remove(routeItem.getClass());
    }


    private void initViewForSearch() {
        View editFrame = binding.searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
        binding.searchView.setOnSearchClickListener(v -> {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.searchView.getLayoutParams();
            lp.startToEnd = binding.svAppListFilter.getId();
            binding.searchView.setLayoutParams(lp);
        });

        binding.searchView.setOnCloseListener(() -> {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.searchView.getLayoutParams();
            lp.startToEnd = ConstraintLayout.LayoutParams.UNSET;
            binding.searchView.setLayoutParams(lp);
            return false;
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryAndShowAppListView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryAndShowAppListView(newText);
                return false;
            }

        });

        binding.svAppListFilter.setOnMenuShowListener(popupMenu -> {
            MenuItem filterMenuItem = popupMenu.getMenu().findItem(filterActionId);
            if (filterMenuItem != null) {
                filterMenuItem.setChecked(true);
            }
            MenuItem sortMenuItem = popupMenu.getMenu().findItem(sortActionId);
            if (sortMenuItem != null) {
                sortMenuItem.setChecked(true);
            }
        });

        binding.svAppListFilter.setOnMenuItemClickListener(item -> {
            item.setChecked(true);
            int groupId = item.getGroupId();
            if (groupId == R.id.group_sort) {
                sortActionId = item.getItemId();
            } else if (groupId == R.id.group_filter) {
                filterActionId = item.getItemId();
            }
            updateInstallInfoList();
            return false;
        });

    }

    private void initViewForAppList() {
        appListAdapter = new MultiAdapter<AppListModel>() {

        }.setDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!isFinishing() || isDestroyed()) {
                    return;
                }
                binding.tvAppCount.setText("数量：" + appListAdapter.getData().size() + "");
            }
        }).addItemType(new SimpleItemType<AppListModel>() {
            @NonNull
            @Override
            public MultiViewHolder<AppListModel> createViewHolder(@NonNull MultiAdapter<AppListModel> adapter, @NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(com.lu.magic.base.R.layout.item_enable_list, parent, false);
                return new ItemViewHolder(v);
            }
        });

        RecyclerView rvAppList = binding.rvAppList;
        rvAppList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvAppList.setAdapter(appListAdapter);

    }


    private void updateInstallInfoList() {
        WorkerUtil.loadSingle(() -> {
            List<AppListModel> filterApp = filterApp(installAppModelMap);
            sortApp(filterApp);
            return filterApp;
        }).intoMain(appListModels -> {
            appListAdapter.updateData(appListModels);
            binding.rvAppList.smoothScrollToPosition(0);
        });

    }

    private void queryAndShowAppListView(String keyWord) {
        List<AppListModel> filterApp = filterApp(installAppModelMap, keyWord);
        sortApp(filterApp);
        appListAdapter.updateData(filterApp);
    }

    private List<AppListModel> filterApp(Map<String, AppListModel> appInfoMap) {
        return filterApp(appInfoMap, "");
    }

    private List<AppListModel> filterApp(Map<String, AppListModel> appInfoMap, String keyWord) {
        Set<Map.Entry<String, AppListModel>> entrySet = appInfoMap.entrySet();
        int filterId = filterActionId;
        List<AppListModel> appListModels = new ArrayList<>();
        Iterator<Map.Entry<String, AppListModel>> it = entrySet.iterator();
        String upKeyWord = keyWord.toUpperCase();
        while (it.hasNext()) {
            Map.Entry<String, AppListModel> item = it.next();
            AppListModel model = item.getValue();
            PackageInfo pkgInfo = model.getPackageInfo();

            //去掉不包含关键字的包,忽略大小写
            if (!TextUtils.isEmpty(upKeyWord)
                    && !model.getName().toUpperCase().contains(upKeyWord)
                    && !model.getPackageName().toUpperCase().contains(upKeyWord)) {
                continue;
            }

            if (filterId == R.id.filter_only_show_system_app) {
                if (PackageUtil.Companion.isSystemApp(pkgInfo)) {
                    appListModels.add(model);
                }
            } else if (filterId == R.id.filter_hide_system_app) {
                if (!PackageUtil.Companion.isSystemApp(pkgInfo)) {
                    appListModels.add(model);
                }
            } else if (filterId == R.id.filter_only_show_debug_app) {
                if (PackageUtil.Companion.isDebugApp(pkgInfo)) {
                    appListModels.add(model);
                }
            } else {
                appListModels.add(model);
            }
        }

        return appListModels;
    }

    private void sortApp(List<AppListModel> appListModels) {
        int sortId = sortActionId;
        Collections.sort(appListModels, (o1, o2) -> {
            //打开开关的在前，所以index小
            if (o1.getEnable() && !o2.getEnable()) {
                return -1;
            }
            if (o2.getEnable() && !o1.getEnable()) {
                return 1;
            }
            if (sortId == R.id.sort_app_name_A_Z) {//都是enable或都不是
                return o1.getName().compareTo(o2.getName());
            } else if (sortId == R.id.sort_app_name_Z_A) {
                return o2.getName().compareTo(o1.getName());
            } else if (sortId == R.id.sort_package_name_A_Z) {
                return o1.getPackageName().compareTo(o2.getPackageName());
            } else if (sortId == R.id.sort_package_name_Z_A) {
                return o2.getPackageName().compareTo(o1.getPackageName());
            }
            return 0;
        });

    }


    private void loadInstallInfoList() {
        WorkerUtil.loadSingle(() -> {
            HashMap<String, AppListModel> appModelMap = new HashMap<>();
            Map<String, BaseConfig> enableMap = ConfigUtil.getSheet(routeItem.getModule().getModuleId(), BaseConfig.class);

            List<PackageInfo> installInfoList = PackageUtil.Companion.getInstallPackageInfoList(getContext());
            for (PackageInfo packageInfo : installInfoList) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
//                耗时操作，放到其他线程中，以免ui显示等待较长，这里只拿基本的信息
//                String appName = appInfo.loadLabel(getContext().getPackageManager()) + "";
                String appName = appInfo.name == null ? "" : appInfo.name;
                String packageName = appInfo.packageName == null ? "" : appInfo.packageName;

                BaseConfig configBean = enableMap.get(packageName);
                boolean enable = configBean != null && configBean.isEnable();
                AppListModel model = new AppListModel(packageInfo, appName, packageName, null, enable);
                appModelMap.put(packageName, model);
            }
            installAppModelMap.putAll(appModelMap);
            List<AppListModel> filterList = filterApp(appModelMap);
            sortApp(filterList);
            return filterList;
        }).intoMain(filterList -> {
            appListAdapter.updateData(filterList);
            binding.rvAppList.smoothScrollToPosition(0);

            updateAppNameAsync();
        });

    }

    private void updateAppNameAsync() {
        HashMap<String, AppListModel> local = installAppModelMap;
        WorkerUtil.executeSingle(() -> {
            Set<Map.Entry<String, AppListModel>> entrySet = local.entrySet();
            Iterator<Map.Entry<String, AppListModel>> it = entrySet.iterator();
            PackageManager pm = getContext().getPackageManager();
            while (it.hasNext()) {
                Map.Entry<String, AppListModel> ele = it.next();
                AppListModel model = ele.getValue();
                ApplicationInfo appInfo = model.getPackageInfo().applicationInfo;
                String appName = appInfo.loadLabel(pm) + "";
                model.setName(appName);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_app, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private class ItemViewHolder extends MultiViewHolder<AppListModel> {
        private final LoaderCacheUtil.ObjectLoader<AppListModel> appListModelLoader;
        private TextView tvPackageName;
        private TextView tvAppName;
        private ImageView ivAppIcon;
        private SwitchButton sbEnableItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            appListModelLoader = LoaderCacheUtil.createObjectLoader(new LruCache<String, AppListModel>(100));

            ivAppIcon = itemView.findViewById(com.lu.magic.base.R.id.ivHeadIcon);
            tvAppName = itemView.findViewById(com.lu.magic.base.R.id.tvBodyTitle);
            tvPackageName = itemView.findViewById(com.lu.magic.base.R.id.tvBodySubTitle);
            sbEnableItem = itemView.findViewById(com.lu.magic.base.R.id.sbEnableItem);

            itemView.setOnClickListener(v -> {
                int clickPosition = getLayoutPosition();
                AppListModel itemData = appListAdapter.getItem(clickPosition);
                IModuleFace module = ModuleProviders.INSTANCE.get(routeItem.getModule().getModuleId());
                if (module == null || module.getDetailFragmentFactory() == null) {
                    return;
                }
                AppRouter.INSTANCE.routeDetailConfigPage(SelectAppActivity.this, routeItem, itemData);
            });
            sbEnableItem.setOnClickListener(v -> {
                CompoundButton compoundButton = ((CompoundButton) v);
                boolean check = compoundButton.isChecked();
                int clickPosition = getLayoutPosition();
                AppListModel itemData = appListAdapter.getItem(clickPosition);
                itemData.setEnable(check);
                String packageName = itemData.getPackageName();
                //打开指定表的配置
                ConfigUtil.enableCell(routeItem.getModule().getModuleId(), packageName, check);
            });
        }

        @Override
        public void onBindView(@NonNull MultiAdapter<AppListModel> adapter, AppListModel itemModel, int position) {
            tvAppName.setText(itemModel.getName());
            tvPackageName.setText(itemModel.getPackageName());
            sbEnableItem.setChecked(itemModel.getEnable());

            //每次加载前都设置tag
            ivAppIcon.setTag(position);
            appListModelLoader.loadIO(itemModel.getPackageName(), () -> {
                AppListModel model = installAppModelMap.get(itemModel.getPackageName());
                PackageInfo packageInfo = model.getPackageInfo();
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                PackageManager pm = getContext().getPackageManager();

                Drawable drawable = appInfo.loadIcon(pm);
                if (TextUtils.isEmpty(model.getName()) || model.getName().equals(appInfo.name)) {
                    String label = appInfo.loadLabel(pm) + "";
                    model.setName(label);
                }
                model.setIcon(drawable);
                return model;
            }).intoMain(appListModel -> {
                Object tag = ivAppIcon.getTag();
                if (!Integer.valueOf(position).equals(tag)) {
                    //tag不相等，说明异步加载出的图片不是要展示的。
                    //加载需要时间，划view到不可见后，被view复用时，会重新走到设置tag
                    //忽略掉，防止错位、闪现
                    return;
                }
//                LogUtil.d(">>>position", position);
                tvAppName.setText(appListModel.getName());
                ivAppIcon.setImageDrawable(appListModel.getIcon());
                tvPackageName.setText(appListModel.getPackageName());

            });
        }
    }


}




