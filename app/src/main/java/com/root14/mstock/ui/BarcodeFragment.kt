package com.root14.mstock.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import com.root14.mstock.data.IMStockBarcodeScanner
import com.root14.mstock.data.MStockBarcodeScanner
import com.root14.mstock.data.enum.ErrorType
import com.root14.mstock.databinding.FragmentBarcodeBinding
import com.root14.mstock.viewmodel.LoginViewModel
import com.root14.mstock.viewmodel.MainViewModel


class BarcodeFragment : Fragment() {
    private var _binding: FragmentBarcodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mStockBarcodeScanner: MStockBarcodeScanner

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStockBarcodeScanner = MStockBarcodeScanner()

        mStockBarcodeScanner.addContext(requireContext())
        mStockBarcodeScanner.build()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeBinding.inflate(inflater, container, false)

        mStockBarcodeScanner.addPermission(Manifest.permission.ACCEPT_HANDOVER)
            .requestPermission(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        mStockBarcodeScanner.bindToView(binding.previewView)


        binding.buttonReadBarcode.setOnClickListener {
            mainViewModel._DB_DEBUG_()
            mStockBarcodeScanner.processPhoto(object : IMStockBarcodeScanner {
                override fun onBarcodeSuccess(barcodes: MutableList<Barcode>) {
                    Snackbar.make(
                        binding.root, barcodes[0].rawValue.toString(), Snackbar.LENGTH_SHORT
                    ).show()


                }

                override fun onBarcodeFailure(barcodeOnFailure: ErrorType, e: Exception) {
                    Snackbar.make(
                        binding.root,
                        "Barcode could not be read. Try it again.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onBarcodeComplete(
                    barcodeOnComplete: ErrorType, task: Task<MutableList<Barcode>>
                ) {
                    // Snackbar.make(binding.root, "onBarcodeComplete", Snackbar.LENGTH_SHORT).show()
                }
            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mStockBarcodeScanner.unbind()
    }
}