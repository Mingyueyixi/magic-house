package com.lu.code.magic.main.screen;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kyleduo.switchbutton.SwitchButton;
import com.lu.code.magic.bean.BaseConfig;
import com.lu.code.magic.magic.R;
import com.lu.code.magic.magic.databinding.FragScreenOrientationBinding;
import com.lu.code.magic.main.AppListModel;
import com.lu.code.magic.main.store.ItemModel;
import com.lu.code.magic.ui.BindingFragment;
import com.lu.code.magic.ui.recycler.MultiAdapter;
import com.lu.code.magic.ui.recycler.MultiViewHolder;
import com.lu.code.magic.ui.recycler.SimpleItemType;
import com.lu.code.magic.ui.view.ItemMoveLayout;
import com.lu.code.magic.util.GsonUtil;
import com.lu.code.magic.util.SingleStoreUtil;
import com.lu.code.magic.util.TextUtil;
import com.lu.code.magic.util.ToastUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;
import com.lu.code.magic.util.dialog.DialogUtil;
import com.lu.code.magic.util.dialog.EditDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScreenOrientationFragment extends BindingFragment<FragScreenOrientationBinding> {
    private ItemModel routeItem;
    private AppListModel selectPkg;
    private BaseConfig<OrientationDTO> mViewConfig;
    private ViewStateModel viewModel;
    private MultiAdapter<OrientationDTO.ActItem> templateAdapter;

    @NonNull
    @Override
    public FragScreenOrientationBinding onViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragScreenOrientationBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(ViewStateModel.class);
        routeItem = SingleStoreUtil.get(ItemModel.class);
        selectPkg = SingleStoreUtil.get(AppListModel.class);
        mViewConfig = loadConfig();

        if (mViewConfig == null) {
            mViewConfig = new BaseConfig<>();
        }
        OrientationDTO dataDTO = mViewConfig.getData();
        if (dataDTO == null) {
            //创建默认数据
            OrientationDTO.ActItem item = new OrientationDTO.ActItem(Activity.class.getName(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, false);
            dataDTO = new OrientationDTO(new ArrayList<>());
            dataDTO.getActList().add(item);
            mViewConfig.setData(dataDTO);
        }

    }

    private void initView() {
        initTemplateList();
        viewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
            OrientationDTO.ActItem actItem = viewState.editActItem;
            int position = viewState.editActItemPosition;
            if (actItem != null) {
                getBinding().tvActClassText.setText(actItem.getActClass());
                getBinding().tvActModeText.setText(ScreenOrientationUtil.getText(actItem.getOrientation()));
            }
            getBinding().rvActivityList.getAdapter().notifyItemChanged(position);
        });

        getBinding().tvAddListItem.setOnClickListener(v -> {
            List<OrientationDTO.ActItem> dataList = templateAdapter.getData();
            showEditView(dataList.size(), new OrientationDTO.ActItem("", -1, true));
        });

    }

    private void initTemplateList() {
        OrientationDTO dataDTO = mViewConfig.getData();
        getBinding().rvActivityList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        templateAdapter = new MultiAdapter<OrientationDTO.ActItem>()
                .setData(dataDTO.getActList())
                .setDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        //同步数据
                        mViewConfig.getData().setActList(templateAdapter.getData());
                    }
                })
                .addItemType(new SimpleItemType<OrientationDTO.ActItem>() {
                    @NonNull
                    @Override
                    public MultiViewHolder<OrientationDTO.ActItem> createViewHolder(@NonNull MultiAdapter<OrientationDTO.ActItem> adapter, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enable_list, parent, false);

                        return new MultiViewHolder<OrientationDTO.ActItem>(view) {
                            private RelativeLayout vBottomLayout;
                            private TextView tvTitle;
                            private TextView tvSubTitle;
                            private ImageView ivHeadView;
                            private SwitchButton sbCheckView;
                            private ViewGroup vItemFace;

                            @Override
                            public void onInit() {
                                tvTitle = itemView.findViewById(R.id.tvBodyTitle);
                                tvSubTitle = itemView.findViewById(R.id.tvBodySubTitle);
                                ivHeadView = itemView.findViewById(R.id.ivHeadIcon);
                                ivHeadView.setVisibility(View.GONE);
                                sbCheckView = itemView.findViewById(R.id.sbEnableItem);
                                vItemFace = itemView.findViewById(R.id.layoutItemFace);
                                vBottomLayout = itemView.findViewById(R.id.vItemBottom);
                                vBottomLayout.setVisibility(View.VISIBLE);

                                itemView.setOnClickListener(v -> {
                                    int position = getLayoutPosition();
                                    List<OrientationDTO.ActItem> dataList = adapter.getData();
                                    OrientationDTO.ActItem actItem = dataList.get(position);
                                    showEditView(position, actItem);
                                });

                                sbCheckView.setOnClickListener(v -> {
                                    int position = getLayoutPosition();
                                    List<OrientationDTO.ActItem> dataList = adapter.getData();
                                    OrientationDTO.ActItem actItem = dataList.get(position);
                                    actItem.setEnable(sbCheckView.isChecked());
                                });
                                //https://blog.csdn.net/fjnu_se/article/details/121896299
                                //RecyclerView可以使用ItemTouchHelper实现侧滑删除效果
                                ItemMoveLayout menuLayout = (ItemMoveLayout) itemView;
                                menuLayout.setMoveView(vItemFace);
                                //侧滑删除，自定义Item View实现
                                menuLayout.setOnFlyListener(new ItemMoveLayout.OnFlyListener() {
                                    private int position;

                                    @Override
                                    public void onStart(View view) {
                                        position = getLayoutPosition();
                                    }

                                    @Override
                                    public void onFlying(View view, float offsetX) {

                                    }

                                    @Override
                                    public void onComplete(View view) {
                                        //侧滑删除
                                        adapter.getData().remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }
                                });
                                menuLayout.setOnTouchTranslateListener((view1, tranX) -> {
                                    //设置删除textView显示位置。显示在坐标还是右边。
                                    //可以两边都设置TextView，根据情况隐藏
                                    int gravity = vBottomLayout.getGravity();
                                    if (tranX > 0) {
                                        if (gravity != Gravity.LEFT) {
                                            vBottomLayout.setGravity(Gravity.LEFT);
                                        }
                                    } else {
                                        if (gravity != Gravity.RIGHT) {
                                            vBottomLayout.setGravity(Gravity.RIGHT);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onBindView(@NonNull MultiAdapter<OrientationDTO.ActItem> adapter, OrientationDTO.ActItem itemModel, int position) {
                                tvTitle.setText(itemModel.getActClass());
                                int subTextId = ScreenOrientationUtil.getText(itemModel.getOrientation());
                                vItemFace.setTranslationX(0);
                                tvSubTitle.setText(subTextId);
                                sbCheckView.setChecked(itemModel.getEnable());
                            }
                        };
                    }
                });
        getBinding().rvActivityList.setAdapter(templateAdapter);
    }


    private BaseConfig<OrientationDTO> loadConfig() {
        Type configType = GsonUtil.getType(BaseConfig.class, OrientationDTO.class);
        return ConfigUtil.getCellForType(SheetName.FUCK_SCREEN_ORIENTATION, selectPkg.getPackageName(), configType);
    }

    private void saveConfig() {
        ConfigUtil.setCell(SheetName.FUCK_SCREEN_ORIENTATION, selectPkg.getPackageName(), mViewConfig);
    }

    private boolean checkHasChangeConfig() {
        JsonElement localJson = ConfigUtil.getCellForType(SheetName.FUCK_SCREEN_ORIENTATION, selectPkg.getPackageName(), JsonObject.class);
        JsonElement viewJson = GsonUtil.toJsonTree(mViewConfig);
        if (("" + localJson).equals("" + viewJson)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (getBinding().layoutBottomMenu.getVisibility() == View.VISIBLE) {
            hideEditView();
            return true;
        }

        //同步数据
        mViewConfig.getData().setActList(templateAdapter.getData());
        if (!checkHasChangeConfig()) {
            return super.onBackPressed();
        }
        DialogUtil.buildAlertDialog(getContext())
                .setTitle("提示")
                .setMessage("是否保存修改？")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("保存", (dialog, which) -> {
                    saveConfig();
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> getActivity().finish())
                .show();
        return true;
    }

    private void hideEditView() {
        getBinding().layoutBottomMenu.setVisibility(View.VISIBLE);
        ValueAnimator ani = ValueAnimator.ofFloat(0f, 1f);
        ani.setInterpolator(new AccelerateInterpolator());
        ani.setDuration(200);
        ani.addUpdateListener(animation -> {
            float v = (float) animation.getAnimatedValue();
            getBinding().guideline.setGuidelinePercent(v);
            if (v >= 1f) {
                getBinding().layoutBottomMenu.setVisibility(View.GONE);
            }
        });
        ani.start();
    }

    private void showEditView(int position, OrientationDTO.ActItem actItem) {
        getBinding().tvActClassText.setText(TextUtil.ofNotNull(actItem.getActClass()));
        getBinding().tvActModeText.setText(ScreenOrientationUtil.getText(actItem.getOrientation()));

        getBinding().layoutBottomMenu.setVisibility(View.VISIBLE);
        ValueAnimator ani = ValueAnimator.ofFloat(1f, 0f);
        ani.setInterpolator(new AccelerateInterpolator());
        ani.setDuration(200);
        ani.addUpdateListener(animation -> {
            float v = (float) animation.getAnimatedValue();
            getBinding().guideline.setGuidelinePercent(v);
        });
        ani.start();

        getBinding().layoutActClass.setOnClickListener(v -> {
            showEditActClassDialog(position, actItem);
        });

        getBinding().layoutOrientation.setOnClickListener(v -> {
            showEditActOrientationDialog(position, actItem);
        });

        //确定，将数据加入列表
        getBinding().btnBottomComplete.setOnClickListener(v -> {
            addActItemToListView(actItem, position);
        });
    }

    private void addActItemToListView(OrientationDTO.ActItem actItem, int position) {
        if (TextUtil.isEmpty(actItem.getActClass())) {
            ToastUtil.show("Activity不能为空");
            return;
        }
        List<OrientationDTO.ActItem> dataList = templateAdapter.getData();
        if (position == templateAdapter.getData().size()) {
            //新增的重复存在了
            if (checkHasOnActItem(actItem.getActClass(), dataList)) {
                ToastUtil.show("无法添加，Activity已存在");
                return;
            }
            templateAdapter.addData(actItem);
            templateAdapter.notifyItemInserted(position);
        } else {
            ArrayList<OrientationDTO.ActItem> noCurrList = new ArrayList<>(dataList);
            noCurrList.remove(position);
            if (checkHasOnActItem(actItem.getActClass(), noCurrList)) {
                ToastUtil.show("修改失败，Activity已存在");
                return;
            }
            templateAdapter.notifyItemChanged(position);
        }
        hideEditView();
    }

    private void showEditActClassDialog(int position, OrientationDTO.ActItem actItem) {
        EditDialog.Builder builder = DialogUtil.buildEditDialog(getContext());
        String actClass = actItem.getActClass();
        List<OrientationDTO.ActItem> dataList = templateAdapter.getData();

        builder.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && TextUtil.isEmpty(builder.getEditText().getText())) {
                    builder.getEditText().setText("android.content.Activity");
                }
            }
        });

        builder.setTitle("设置Activity的class")
                .setContent(actClass)
                .setContentHint("例如：android.content.Activity")
                .setPositiveButton("确定", (dialog, which) -> {
                    String text = builder.getEditText().getText().toString();
                    if (TextUtil.isEmpty(text)) {
                        ToastUtil.show("Activity不能为空");
                        return;
                    }
                    if (position == templateAdapter.getData().size()) {
                        //新增
                        if (checkHasOnActItem(text, dataList)) {
                            dialog.dismiss();
                            ToastUtil.show("添加失败，Activity已存在");
                            return;
                        }
                    } else {
                        List<OrientationDTO.ActItem> noCurrList = new ArrayList<>(dataList);
                        noCurrList.remove(position);
                        if (checkHasOnActItem(text, noCurrList)) {
                            dialog.dismiss();
                            ToastUtil.show("修改失败，Activity已存在");
                            return;
                        }
                    }
                    actItem.setActClass(text);
                    ViewStateModel.ViewState viewState = viewModel.getViewState();
                    viewState.editActItemPosition = position;
                    viewState.editActItem = actItem;
                    viewModel.setViewState(viewState);

                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();

    }

    private boolean checkHasOnActItem(String actClass, List<OrientationDTO.ActItem> data) {
        for (OrientationDTO.ActItem ele : data) {
            if (actClass.equals(ele.getActClass())) {
                return true;
            }
        }
        return false;
    }

    private void showEditActOrientationDialog(int position, OrientationDTO.ActItem actItem) {
        int[] modeList = new int[]{
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_SENSOR,
                ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
                ActivityInfo.SCREEN_ORIENTATION_NOSENSOR,
                ActivityInfo.SCREEN_ORIENTATION_USER,
                ActivityInfo.SCREEN_ORIENTATION_FULL_USER,
                ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
        };

        String[] textArray = new String[modeList.length];

        Context context = getContext();
        int checkIndex = -1;
        for (int i = 0; i < modeList.length; i++) {
            textArray[i] = ScreenOrientationUtil.getText(context, modeList[i]);
            if (actItem.getOrientation() == modeList[i]) {
                checkIndex = i;
            }
        }

        AlertDialog alertDialog = DialogUtil.buildAlertDialog(getContext())
                .setTitle("屏幕方向")
                .setSingleChoiceItems(textArray, checkIndex, (dialog, which) -> {
                    actItem.setOrientation(modeList[which]);
                    ViewStateModel.ViewState viewState = viewModel.getViewState();
                    viewState.editActItemPosition = position;
                    viewState.editActItem = actItem;
                    viewModel.setViewState(viewState);
                    dialog.dismiss();
                })
                .show();

        int height = (int) (getBinding().getRoot().getHeight() * 0.75f);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height);
    }
}
