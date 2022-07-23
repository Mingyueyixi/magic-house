package com.lu.magic.util.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.magic.util.SizeUtil;

public class EditDialog {

    public static View wrapEditView(EditText editText) {
        Context context = editText.getContext();
        int top = (int) SizeUtil.dp2px(context.getResources(), 24f);
        int left = (int) SizeUtil.dp2px(context.getResources(), 24f);
        int padding = (int) SizeUtil.dp2px(context.getResources(), 6);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.setPadding(left, top, left, 0);

        editText.setPadding(padding, padding, padding, padding);
        editText.setTextSize(14);
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setSingleLine(true);

        frameLayout.addView(editText);
        return frameLayout;
    }

    public static class Builder extends AlertDialog.Builder {
        private CharSequence content;
        private CharSequence contentHint;
        private EditText editText;
        private OnBuilderBeforeListener beforeCreateListener;
        private OnBuilderAfterListener afterCreateListener;
        private TextWatcher onTextChangeListener;

        public Builder(@NonNull Context context) {
            super(context);
            this.editText = new EditText(context);
        }

        public Builder setContent(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder setContentHint(CharSequence contentHint) {
            this.contentHint = contentHint;
            return this;
        }

        @Override
        public Builder setTitle(int titleId) {
            super.setTitle(titleId);
            return this;
        }

        @Override
        public Builder setTitle(@Nullable CharSequence title) {
            super.setTitle(title);
            return this;
        }


        @Override
        public Builder setMessage(int messageId) {
            super.setMessage(messageId);
            return this;
        }

        @Override
        public Builder setMessage(@Nullable CharSequence message) {
            super.setMessage(message);
            return this;
        }

        public EditText getEditText() {
            return editText;
        }

        public Builder setOnTextChangeListener(TextWatcher listener) {
            this.onTextChangeListener = listener;
            return this;
        }
        @NonNull
        @Override
        public AlertDialog create() {
            if (beforeCreateListener != null) {
                beforeCreateListener.before(this);
            }
            if (this.onTextChangeListener != null) {
                editText.addTextChangedListener(this.onTextChangeListener);
            }
            editText.setText(content);
            editText.setHint(contentHint);
            View viewGroup = wrapEditView(editText);
            setView(viewGroup);

            AlertDialog dialog = super.create();

            if (afterCreateListener != null) {
                afterCreateListener.after(this);
            }
            return dialog;
        }

    }

    public interface OnBuilderBeforeListener {
        void before(Builder builder);
    }

    public interface OnBuilderAfterListener {
        void after(Builder builder);
    }
}
