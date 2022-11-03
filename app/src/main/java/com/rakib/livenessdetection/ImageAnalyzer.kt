package com.rakib.livenessdetection

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector

class ImageAnalyzer(
    private val detector: FaceDetector,
    private val context: Context?,
    private val updateViewCallback: UpdateViewCallback
) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Pass Image to ML Kit Vision API
            detector.process(image).addOnSuccessListener { faces ->
                // Task Completed Successfully
                for (face in faces) {
                    // If Classification was Enabled:
                    if (face.smilingProbability != null) {
                        val smileProb = face.smilingProbability
                        if (smileProb!! >= 0.7) {
                            // Smiling
                            updateViewCallback.updateSmileText(context?.getString(R.string.smiling))
                        } else {
                            // Not Smiling
                            updateViewCallback.updateSmileText(context?.getString(R.string.not_smiling))
                        }
                    }

                    var rightEyeOpenProb = 1.0F // Right Eye is Fully Open
                    var leftEyeOpenProb = 1.0F // Left Eye is Fully Open

                    if (face.rightEyeOpenProbability != null) {
                        rightEyeOpenProb = face.rightEyeOpenProbability!!
                    }

                    if (face.leftEyeOpenProbability != null) {
                        leftEyeOpenProb = face.leftEyeOpenProbability!!
                    }

                    if (rightEyeOpenProb <= .1 && leftEyeOpenProb <= .1) {
                        // Both Eyes Blinking
                        updateViewCallback.updateBlinkText(context?.getString(R.string.both_eyes_blinking))
                    } else if (rightEyeOpenProb <= .1) {
                        // Left Eye Blinking
                        updateViewCallback.updateBlinkText(context?.getString(R.string.left_eye_blinking))
                    } else if (leftEyeOpenProb <= .1) {
                        // Right Eye Blinking
                        updateViewCallback.updateBlinkText(context?.getString(R.string.right_eye_blinking))
                    } else {
                        // Not Blinking
                        updateViewCallback.updateBlinkText(context?.getString(R.string.not_blinking))
                    }
                }
            }.addOnCompleteListener {
                imageProxy.close()
            }.addOnFailureListener { e ->
                // Task Failed with an Exception
                e.printStackTrace()
            }
        }
    }
}