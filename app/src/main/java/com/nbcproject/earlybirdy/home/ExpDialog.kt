package com.nbcproject.earlybirdy.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.nbcproject.earlybirdy.databinding.DialogExpBinding

class ExpDialog (
    context: Context, private val data: Int
) : Dialog(context) {

    private lateinit var binding : DialogExpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    private fun initViews() = with(binding) {

        when (data) {
            10 -> {
                binding.tvFhdExp.text = "다음 번에는 더 나은 결과를 얻을 거에요!"
            }
            20 -> {
                binding.tvFhdExp.text = "정말 훌륭한 일을 해냈어요! 앞으로 한걸음 남았습니다!."
            }
            30 -> {
                binding.tvFhdExp.text = "당신의 최고 성과에 도달하셨습니다!. 축하드립니다!"
            }
            else -> {
                binding.tvFhdExp.text = "오늘은 아쉬웠지만, 내일은 더 나아져 미라클모닝을 해봐요!"
            }
        }

        binding.tvFhdExp2.text = "EXP ${data} 획득!"

        binding.icFhdShare.setOnClickListener {
            val sendText = "share test"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, sendText)
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, "Share"))
        }

        binding.btnFhdOk.setOnClickListener {
            dismiss()
        }
    }
}
