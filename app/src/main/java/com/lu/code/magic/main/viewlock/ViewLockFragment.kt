package com.lu.code.magic.main.viewlock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lu.code.magic.bean.ViewLockConfig
import com.lu.code.magic.R
import com.lu.code.magic.databinding.FragmentLockViewBinding
import com.lu.code.magic.main.AppListModel
import com.lu.code.magic.ui.BindingFragment
import com.lu.code.magic.util.SingleStoreUtil
import com.lu.code.magic.util.config.ConfigUtil
import com.lu.code.magic.util.config.SheetName

class ViewLockFragment : BindingFragment<FragmentLockViewBinding>() {
    private lateinit var mAppModel: AppListModel
    private lateinit var config: ViewLockConfig

    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLockViewBinding {
        mAppModel = SingleStoreUtil.get(AppListModel::class.java)
        var config = ConfigUtil.getCell(
            SheetName.VIEW_LOCK,
            mAppModel.packageName,
            ViewLockConfig::class.java
        )
        if (config == null) {
            config = ViewLockConfig()
        }
        this.config = config
        if (config.data == null) {
            config.data = ViewLockConfig.LockTextRule()
        }
        return FragmentLockViewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var rule = config.data
        binding.etSearchKeyWord.setText(rule.keyWord)
        binding.cbDotLineOption.isChecked = rule.matchDotLine


        if ("regex".equals(rule.kwMode)) {
            binding.rbRegexMode.isChecked = true
        } else {
            binding.cbDotLineOption.isEnabled = false
            binding.rbNormalMode.isChecked = true
        }
        binding.etTipMess.setText(rule.mess)
        binding.etPassword.setText(rule.password)
        binding.sbDeepCheck.isChecked = rule.deepCheck

        binding.rgSelectMode.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbNormalMode.id -> {
                    config.data.kwMode = "normal"
                    binding.cbDotLineOption.isEnabled = false
                }
                binding.rbRegexMode.id -> {
                    config.data.kwMode = "regex"
                    binding.cbDotLineOption.isEnabled = true
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            if (binding.rbRegexMode.isChecked) {
                rule.kwMode = "regex"
            } else {
                rule.kwMode = "normal"
            }
            rule.password = binding.etPassword.text.toString()
            rule.keyWord = binding.etSearchKeyWord.text.toString()
            rule.matchDotLine = binding.cbDotLineOption.isChecked
            rule.mess = binding.etTipMess.text.toString()
            rule.deepCheck = binding.sbDeepCheck.isChecked
            saveConfig()
        }


        rule.lockAll.let {
            var menu = binding.svLockMode.popMenu.menu
            if (it) {
                binding.svLockMode.setTipText(menu.findItem(R.id.lock_view_all).title)
                binding.layoutKeyWord.visibility = View.GONE
            } else {
                binding.svLockMode.setTipText(menu.findItem(R.id.lock_view_kw).title)
                binding.layoutKeyWord.visibility = View.VISIBLE
            }
        }

        binding.svLockMode.setOnMenuItemClickListener {
            it.isChecked = true
            when (it.itemId) {
                R.id.lock_view_all -> {
                    binding.layoutKeyWord.visibility = View.GONE
                    binding.svLockMode.setTipText(it.title)
                    config.data.lockAll = true
                }
                R.id.lock_view_kw -> {
                    binding.layoutKeyWord.visibility = View.VISIBLE
                    binding.svLockMode.setTipText(it.title)
                    config.data.lockAll = false
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    override fun onBackPressed(): Boolean {
        saveConfig()
        return super.onBackPressed()
    }

    private fun saveConfig() {
        ConfigUtil.setCell(
            SheetName.VIEW_LOCK,
            mAppModel.packageName,
            config
        )
    }
}
