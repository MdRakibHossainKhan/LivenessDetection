package com.rakib.livenessdetection

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector

class ImageAnalyzer(private val detector: FaceDetector, private val context: Context?) :
    ImageAnalysis.Analyzer {
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
                            // TODO
                        } else {
                            // Not Smiling
                            // TODO
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
                        // TODO
                    } else if (rightEyeOpenProb <= .1) {
                        // Right Eye Blinking
                        // TODO
                    } else if (leftEyeOpenProb <= .1) {
                        // Left Eye Blinking
                        // TODO
                    } else {
                        // Not Blinking
                        // TODO
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