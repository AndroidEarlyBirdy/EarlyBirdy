package com.nbcproject.earlybirdy.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.nbcproject.earlybirdy.databinding.DialogExpBinding
import android.graphics.Bitmap
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider

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
                binding.tvFhdExp.text = "좋습니다! 앞으로 한걸음 남았습니다!."
            }
            30 -> {
                binding.tvFhdExp.text = "당신은 오늘의 최고 성과에 도달하셨습니다!."
            }
            else -> {
                binding.tvFhdExp.text = "내일은 더 노력해서 미라클모닝을 해봐요!"
            }
        }

        binding.tvFhdExp3.text = "EXP ${data} 획득!"

//        binding.icFhdShare.setOnClickListener() {
//            shareScreen()
//        }

        binding.btnFhdOk.setOnClickListener {
            dismiss()
        }
    }

//    private fun shareScreen() {
//        // 화면을 캡쳐하여 Bitmap으로 가져옵니다.
//        val screenshot = takeScreenshot()
//
//        // Bitmap 이미지를 이미지 파일로 저장하지 않고 곧바로 공유합니다.
//        shareBitmap(screenshot)
//    }
//
//    private fun takeScreenshot(): Bitmap {
//        val rootView = window!!.decorView
//        rootView.isDrawingCacheEnabled = true
//        return rootView.drawingCache
//    }
//
//    private fun shareBitmap(bitmap: Bitmap) {
//        val shareIntent = Intent(Intent.ACTION_SEND)
//
//        // 이미지를 ByteArray로 변환합니다.
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//        val byteArray = stream.toByteArray()
//
//        // ByteArray를 공유 인텐트에 첨부합니다.
//        shareIntent.type = "image/*"
//        shareIntent.putExtra(Intent.EXTRA_STREAM, byteArray)
//
//        // 공유 앱을 선택하는 다이얼로그를 표시합니다.
//        context.startActivity(Intent.createChooser(shareIntent, "Share img"))
//    }



}
