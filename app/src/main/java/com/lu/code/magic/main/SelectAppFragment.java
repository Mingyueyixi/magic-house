package com.lu.code.magic.main;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.kyleduo.switchbutton.SwitchButton;
import com.lu.code.magic.bean.BaseConfig;
import com.lu.code.magic.magic.R;
import com.lu.code.magic.magic.databinding.FragmentSelectAppBinding;
import com.lu.code.magic.main.store.ItemModel;
import com.lu.code.magic.ui.BindingFragment;
import com.lu.code.magic.ui.recycler.MultiAdapter;
import com.lu.code.magic.ui.recycler.MultiViewHolder;
import com.lu.code.magic.ui.recycler.SimpleItemType;
import com.lu.code.magic.util.PackageUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;
import com.lu.code.magic.util.load.LoaderCacheUtil;
import com.lu.code.magic.util.log.LogUtil;
import com.lu.code.magic.util.thread.WorkerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectAppFragment extends BindingFragment<FragmentSelectAppBinding> {
    private MultiAdapter<AppListModel> appListAdapter;
    private HashMap<String, AppListModel> installAppModelMap = new HashMap<>();
    private int sortActionId = R.id.sort_package_name_A_Z;
    private int filterActionId = R.id.filter_hide_system_app;

    @NonNull
    @Override
    public FragmentSelectAppBinding onViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentSelectAppBinding.inflate(inflater, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewForAppList();
        initViewForSearch();
        loadInstallInfoList();
    }

    private void initViewForSearch() {
        View editFrame = getBinding().searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);

        getBinding().searchView.setOnSearchClickListener(v -> {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) getBinding().searchView.getLayoutParams();
            lp.startToEnd = getBinding().searchSelectView.getId();
            getBinding().searchView.setLayoutParams(lp);
        });

        getBinding().searchView.setOnCloseListener(() -> {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) getBinding().searchView.getLayoutParams();
            lp.startToEnd = ConstraintLayout.LayoutParams.UNSET;
            getBinding().searchView.setLayoutParams(lp);
            return false;
        });

        getBinding().searchSelectView.setOnMenuShowListener(popupMenu -> {
            MenuItem filterMenuItem = popupMenu.getMenu().findItem(filterActionId);
            if (filterMenuItem != null) {
                filterMenuItem.setChecked(true);
            }
            MenuItem sortMenuItem = popupMenu.getMenu().findItem(sortActionId);
            if (sortMenuItem != null) {
                sortMenuItem.setChecked(true);
            }
        });

        getBinding().searchSelectView.setOnMenuItemClickListener(item -> {
            item.setChecked(true);
            switch (item.getGroupId()) {
                case R.id.group_sort:
                    sortActionId = item.getItemId();
                    break;
                case R.id.group_filter:
                    filterActionId = item.getItemId();
                    break;
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
                //fix，规避kotlin BindingFragment binding!!非空断言导致的崩溃。
                //当横竖屏切换时，fragment重新销毁，binding会被置空，调用binding会崩溃
                if (!isAdded()) {
                    //取消destroy时取消监听，也可规避
                    return;
                }
                getBinding().tvAppCount.setText("数量：" + appListAdapter.getData().size() + "");
            }
        }).addItemType(new SimpleItemType<AppListModel>() {
            @NonNull
            @Override
            public MultiViewHolder<AppListModel> createViewHolder(@NonNull MultiAdapter<AppListModel> adapter, @NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.item_app_list, parent, false);
                return new ItemViewHolder(v);
            }
        });

        RecyclerView rvAppList = getBinding().rvAppList;
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
            getBinding().rvAppList.smoothScrollToPosition(0);
        });

    }

    private List<AppListModel> filterApp(Map<String, AppListModel> pkgInfoMap) {
        Set<Map.Entry<String, AppListModel>> entrySet = pkgInfoMap.entrySet();
        int filterId = filterActionId;
        List<AppListModel> appListModels = new ArrayList<>();
        Iterator<Map.Entry<String, AppListModel>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<String, AppListModel> item = it.next();
            AppListModel model = item.getValue();
            PackageInfo pkgInfo = model.getPackageInfo();
            switch (filterId) {
                case R.id.filter_only_show_system_app:
                    if (PackageUtil.Companion.isSystemApp(pkgInfo)) {
                        appListModels.add(model);
                    }
                    break;
                case R.id.filter_hide_system_app:
                    if (!PackageUtil.Companion.isSystemApp(pkgInfo)) {
                        appListModels.add(model);
                    }
                    break;
                case R.id.filter_only_show_debug_app:
                    if (PackageUtil.Companion.isDebugApp(pkgInfo)) {
                        appListModels.add(model);
                    }
                    break;
                default:
                    appListModels.add(model);
                    break;
            }
        }

        return appListModels;
    }

    private void sortApp(List<AppListModel> appListModels) {
        int sortId = sortActionId;
        Collections.sort(appListModels, (o1, o2) -> {
            switch (sortId) {
                case R.id.sort_app_name_A_Z:
                    return o1.getName().compareTo(o2.getName());
                case R.id.sort_app_name_Z_A:
                    return o2.getName().compareTo(o1.getName());
                case R.id.sort_package_name_A_Z:
                    return o1.getPackageName().compareTo(o2.getPackageName());
                case R.id.sort_package_name_Z_A:
                    return o2.getPackageName().compareTo(o1.getPackageName());
                default:
                    break;
            }
            return 0;
        });
    }


    private void loadInstallInfoList() {
        MagicConfigActivity activity = (MagicConfigActivity) getActivity();
        if (activity == null) {
            return;
        }
        ItemModel routeItem = activity.getRouteItem();

        WorkerUtil.loadSingle(() -> {
            HashMap<String, AppListModel> appModelMap = new HashMap<>();
            Map<String, BaseConfig> enableMap = ConfigUtil.getConfigSheet(routeItem.getPage().getSheet(), BaseConfig.class);

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
            getBinding().rvAppList.smoothScrollToPosition(0);

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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.select_app, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //fix，规避kotlin BindingFragment binding!!非空断言导致的崩溃。
        //当横竖屏切换时，fragment重新销毁，binding会被置空，调用binding会崩溃
        appListAdapter.removeDataObserver();
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

            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            sbEnableItem = itemView.findViewById(R.id.sbEnableItem);

            itemView.setOnClickListener(v -> {
                int clickPosition = getLayoutPosition();
                AppListModel itemData = appListAdapter.getItem(clickPosition);
                FragmentActivity activity = getActivity();
                if (activity instanceof MagicConfigActivity) {
                    ((MagicConfigActivity) activity).showConfigPage(itemData);
                }
            });

//            sbEnableItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
//
//            });

            sbEnableItem.setOnClickListener(v -> {
                CompoundButton compoundButton = ((CompoundButton) v);
                boolean check = compoundButton.isChecked();
                int clickPosition = getLayoutPosition();
                AppListModel itemData = appListAdapter.getItem(clickPosition);
                itemData.setEnable(check);
                String packageName = itemData.getPackageName();
                ConfigUtil.enableConfigCell(SheetName.FUCK_DIALOG, packageName, check);
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
                LogUtil.d(">>>position", position);
                tvAppName.setText(appListModel.getName());
                ivAppIcon.setImageDrawable(appListModel.getIcon());
                tvPackageName.setText(appListModel.getPackageName());

            });


        }
    }


}
