package com.lu.magic.screen;

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
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lu.magic.bean.BaseConfig;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.config.ModuleId;
import com.lu.magic.main.AppListModel;
import com.lu.magic.screen.databinding.FragScreenOrientationBinding;
import com.lu.magic.store.ItemModel;
import com.lu.magic.ui.BindingFragment;
import com.lu.magic.ui.recycler.MultiAdapter;
import com.lu.magic.ui.recycler.MultiViewHolder;
import com.lu.magic.ui.recycler.SimpleItemType;
import com.lu.magic.ui.view.ItemMoveLayout;
import com.lu.magic.util.GsonUtil;
import com.lu.magic.util.SingleClassStoreUtil;
import com.lu.magic.util.TextUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.dialog.DialogUtil;
import com.lu.magic.util.dialog.EditDialog;

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
        routeItem = SingleClassStoreUtil.get(ItemModel.class);
        selectPkg = SingleClassStoreUtil.get(AppListModel.class);
        mViewConfig = loadConfig();

        if (mViewConfig == null) {
            mViewConfig = new BaseConfig<>();
        }
        OrientationDTO dataDTO = mViewConfig.getData();
        if (dataDTO == null) {
            //??????????????????
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
                        //????????????
                        mViewConfig.getData().setActList(templateAdapter.getData());
                    }
                })
                .addItemType(new SimpleItemType<OrientationDTO.ActItem>() {
                    @NonNull
                    @Override
                    public MultiViewHolder<OrientationDTO.ActItem> createViewHolder(@NonNull MultiAdapter<OrientationDTO.ActItem> adapter, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(com.lu.magic.base.R.layout.item_enable_list, parent, false);

                        return new MultiViewHolder<OrientationDTO.ActItem>(view) {
                            private RelativeLayout vBottomLayout;
                            private TextView tvTitle;
                            private TextView tvSubTitle;
                            private ImageView ivHeadView;
                            private SwitchCompat sbCheckView;
                            private ViewGroup vItemFace;

                            @Override
                            public void onInit() {
                                tvTitle = itemView.findViewById(com.lu.magic.base.R.id.tvBodyTitle);
                                tvSubTitle = itemView.findViewById(com.lu.magic.base.R.id.tvBodySubTitle);
                                ivHeadView = itemView.findViewById(com.lu.magic.base.R.id.ivHeadIcon);
                                ivHeadView.setVisibility(View.GONE);
                                sbCheckView = itemView.findViewById(com.lu.magic.base.R.id.sbEnableItem);
                                vItemFace = itemView.findViewById(com.lu.magic.base.R.id.layoutItemFace);
                                 vBottomLayout = itemView.findViewById(com.lu.magic.base.R.id.vItemBottom);
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
                                //RecyclerView????????????ItemTouchHelper????????????????????????
                                ItemMoveLayout menuLayout = (ItemMoveLayout) itemView;
                                menuLayout.setMoveView(vItemFace);
                                //????????????????????????Item View??????
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
                                        //????????????
                                        adapter.getData().remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }
                                });
                                menuLayout.setOnTouchTranslateListener((view1, tranX) -> {
                                    //????????????textView?????????????????????????????????????????????
                                    //?????????????????????TextView?????????????????????
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
        return ConfigUtil.getCellForType(ModuleId.FUCK_SCREEN_ORIENTATION, selectPkg.getPackageName(), configType);
    }

    private void saveConfig() {
        ConfigUtil.setCell(ModuleId.FUCK_SCREEN_ORIENTATION, selectPkg.getPackageName(), mViewConfig);
    }

    private boolean checkHasChangeConfig() {
        JsonElement localJson = ConfigUtil.getCellForType(ModuleId.FUCK_SCREEN_ORIENTATION, selectPkg.getPackageName(), JsonObject.class);
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

        //????????????
        mViewConfig.getData().setActList(templateAdapter.getData());
        if (!checkHasChangeConfig()) {
            return super.onBackPressed();
        }
        DialogUtil.buildAlertDialog(getContext())
                .setTitle("??????")
                .setMessage("?????????????????????")
                .setNegativeButton("??????", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("??????", (dialog, which) -> {
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

        //??????????????????????????????
        getBinding().btnBottomComplete.setOnClickListener(v -> {
            addActItemToListView(actItem, position);
        });
    }

    private void addActItemToListView(OrientationDTO.ActItem actItem, int position) {
        if (TextUtil.isEmpty(actItem.getActClass())) {
            ToastUtil.show("Activity????????????");
            return;
        }
        List<OrientationDTO.ActItem> dataList = templateAdapter.getData();
        if (position == templateAdapter.getData().size()) {
            //????????????????????????
            if (checkHasOnActItem(actItem.getActClass(), dataList)) {
                ToastUtil.show("???????????????Activity?????????");
                return;
            }
            templateAdapter.addData(actItem);
            templateAdapter.notifyItemInserted(position);
        } else {
            ArrayList<OrientationDTO.ActItem> noCurrList = new ArrayList<>(dataList);
            noCurrList.remove(position);
            if (checkHasOnActItem(actItem.getActClass(), noCurrList)) {
                ToastUtil.show("???????????????Activity?????????");
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

        builder.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && TextUtil.isEmpty(builder.getEditText().getText())) {
                builder.getEditText().setText("android.app.Activity");
            }
        });

        builder.setTitle("??????Activity???class")
                .setContent(actClass)
                .setContentHint("?????????android.app.Activity")
                .setPositiveButton("??????", (dialog, which) -> {
                    String text = builder.getEditText().getText().toString();
                    if (TextUtil.isEmpty(text)) {
                        ToastUtil.show("Activity????????????");
                        return;
                    }
                    if (position == templateAdapter.getData().size()) {
                        //??????
                        if (checkHasOnActItem(text, dataList)) {
                            dialog.dismiss();
                            ToastUtil.show("???????????????Activity?????????");
                            return;
                        }
                    } else {
                        List<OrientationDTO.ActItem> noCurrList = new ArrayList<>(dataList);
                        noCurrList.remove(position);
                        if (checkHasOnActItem(text, noCurrList)) {
                            dialog.dismiss();
                            ToastUtil.show("???????????????Activity?????????");
                            return;
                        }
                    }
                    actItem.setActClass(text);
                    ViewStateModel.ViewState viewState = viewModel.getViewState();
                    viewState.editActItemPosition = position;
                    viewState.editActItem = actItem;
                    viewModel.setViewState(viewState);

                })
                .setNegativeButton("??????", (dialog, which) -> dialog.dismiss())
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
                .setTitle("????????????")
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
