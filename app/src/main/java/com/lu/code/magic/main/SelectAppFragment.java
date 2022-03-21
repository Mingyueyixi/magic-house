package com.lu.code.magic.main;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kyleduo.switchbutton.SwitchButton;
import com.lu.code.magic.magic.R;
import com.lu.code.magic.magic.databinding.FragmentSelectAppBinding;
import com.lu.code.magic.ui.BindingFragment;
import com.lu.code.magic.ui.recycler.MultiAdapter;
import com.lu.code.magic.ui.recycler.MultiViewHolder;
import com.lu.code.magic.ui.recycler.SimpleItemType;
import com.lu.code.magic.util.PackageUtil;
import com.lu.code.magic.util.TextUtil;
import com.lu.code.magic.util.load.LoaderCacheUtil;
import com.lu.code.magic.util.log.LogUtil;
import com.lu.code.magic.util.thread.WorkerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectAppFragment extends BindingFragment<FragmentSelectAppBinding> {
    private MultiAdapter<AppListModel> appListAdapter;
    private HashMap<String, PackageInfo> installPackageInfoMap;
    private int sortActionId;
    private int filterActionId;

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

        getBinding().searchSelectView.setOnMenuItemClickListener(item -> {
            item.setChecked(true);
            switch (item.getGroupId()) {
                case R.id.action_sort:
                    sortActionId = item.getItemId();
                    break;
                case R.id.action_filter:
                    filterActionId = item.getItemId();
                    break;
            }
            updateInstallInfoList();
            return false;
        });

        loadInstallInfoList();

    }

    private void updateInstallInfoList() {
        WorkerUtil.loadSingle(() -> {
            List<AppListModel> filterApp = filterApp();
            sortApp(filterApp);
            return filterApp;
        }).intoMain(appListModels -> {
            appListAdapter.updateData(appListModels);
            getBinding().rvAppList.smoothScrollToPosition(0);
        });

    }

    private List<AppListModel> filterApp() {
        Set<Map.Entry<String, PackageInfo>> entrySet = installPackageInfoMap.entrySet();
        int filterId = filterActionId;
        List<AppListModel> appListModels = new ArrayList<>();
        Iterator<Map.Entry<String, PackageInfo>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<String, PackageInfo> item = it.next();
            PackageInfo v = item.getValue();
            String appName = v.applicationInfo.name;
            appName = appName == null ? "" : appName;
            AppListModel model = new AppListModel(appName, v.packageName, false);
            switch (filterId) {
                case R.id.action_only_show_system_app:
                    if (PackageUtil.Companion.isSystemApp(v)) {
                        appListModels.add(model);
                    }
                    break;
                case R.id.action_hide_system_app:
                    if (!PackageUtil.Companion.isSystemApp(v)) {
                        appListModels.add(model);
                    }
                    break;
                case R.id.action_only_show_debug_app:
                    if (PackageUtil.Companion.isDebugApp(v)) {
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
                case R.id.action_sort_app_name_A_Z:
                    return o1.getName().compareTo(o2.getName());
                case R.id.action_sort_app_name_Z_A:
                    return o2.getName().compareTo(o1.getName());
                case R.id.action_sort_package_name_A_Z:
                    return o1.getPackageName().compareTo(o2.getPackageName());
                case R.id.action_sort_package_name_Z_A:
                    return o2.getPackageName().compareTo(o1.getPackageName());
                default:
                    break;
            }
            return 0;
        });
    }


    private void loadInstallInfoList() {
        installPackageInfoMap = new HashMap<>();

        WorkerUtil.loadSingle(() -> {
            List<PackageInfo> installInfoList = PackageUtil.Companion.getInstallPackageInfoList(getContext());
            List<AppListModel> appListModels = new ArrayList<>();
            for (PackageInfo packageInfo : installInfoList) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                String appName = appInfo.name;
                String packageName = appInfo.packageName == null ? "" : appInfo.packageName;
                if (TextUtil.isEmpty(appName)) {
                    appName = appInfo.loadLabel(getContext().getPackageManager()) + "";
                    LogUtil.d("app名为空", packageName);
                    if (TextUtil.isEmpty(appName)) {
                        LogUtil.d("app label名为空", packageName);
                    }
                }

                AppListModel appListModel = new AppListModel(appName, packageName, false);
                if (TextUtil.isEmpty(packageName)) {
                    LogUtil.e("包名为空", appInfo);
                }
                installPackageInfoMap.put(packageName, packageInfo);
                appListModels.add(appListModel);
            }
            return appListModels;
        }).intoMain(appListModels -> {
            appListAdapter.updateData(appListModels);
            getBinding().rvAppList.smoothScrollToPosition(0);
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
        }

        @Override
        public void onBindView(@NonNull MultiAdapter<AppListModel> adapter, AppListModel itemModel, int position) {
            tvAppName.setText(itemModel.getName());
            tvPackageName.setText(itemModel.getPackageName());
            sbEnableItem.setChecked(itemModel.getEnable());

            //每次加载前都设置tag
            ivAppIcon.setTag(position);
            appListModelLoader.loadIO(itemModel.getPackageName(), () -> {
                PackageInfo packageInfo = installPackageInfoMap.get(itemModel.getPackageName());
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                Drawable drawable = appInfo.loadIcon(getContext().getPackageManager());
                String name = appInfo.loadLabel(getContext().getPackageManager()).toString();
                return new AppListModel(name, itemModel.getPackageName(), drawable, itemModel.getEnable());
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
