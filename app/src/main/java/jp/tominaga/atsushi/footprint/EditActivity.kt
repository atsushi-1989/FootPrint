package jp.tominaga.atsushi.footprint

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_edit.*
import kotlin.math.log

class EditActivity : AppCompatActivity() {

    lateinit var mode: ModeInEdit

    val PERMISSION = arrayOf(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION)

    var isCameraEnabled = false
    var isWriteStorageEnabled = false
    var isLocationAccessEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {
                finish()
            }
        }

        mode = intent.extras?.getSerializable(IntentKey.EDIT_MODE.name) as ModeInEdit

        if(mode == ModeInEdit.SHOOT){
            if (Build.VERSION.SDK_INT >=23) permissionCheck() else launchCameta()
        } else {
            //Todo 編集モードでやること
        }


    }


    private fun permissionCheck() {
        val permissionCheckCamera: Int = ContextCompat.checkSelfPermission(this@EditActivity,PERMISSION[0])
        val permissionCheckWriteStorage: Int = ContextCompat.checkSelfPermission(this@EditActivity,PERMISSION[1])
        val permissionCheckLocationAccess: Int = ContextCompat.checkSelfPermission(this@EditActivity,PERMISSION[2])

        if(permissionCheckCamera == PackageManager.PERMISSION_GRANTED) isCameraEnabled = true
        if(permissionCheckWriteStorage == PackageManager.PERMISSION_GRANTED) isWriteStorageEnabled = true
        if(permissionCheckLocationAccess == PackageManager.PERMISSION_GRANTED) isLocationAccessEnabled = true

        if (isCameraEnabled && isWriteStorageEnabled && isLocationAccessEnabled) launchCameta() else permissionRequest()

    }

    private fun permissionRequest() {

        val isNeedExplainForCameraPermission =
            ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity,PERMISSION[0])
        val isNeedExplainForWriteStoreagePermission =
            ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity,PERMISSION[1])
        val isNeedExplainForLocationAccess =
            ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity,PERMISSION[2])

        val isNeedExplainForPermission =
            if(isNeedExplainForCameraPermission || isNeedExplainForWriteStoreagePermission || isNeedExplainForLocationAccess){
                true
            }else false

        //許可をリクエストするリクエストするパーミッションを入れるリストの設定
        val requestPermisstionList = ArrayList<String>()

        //許可させていないパーミッションをリクエストのリストに入れる
        if (!isCameraEnabled) requestPermisstionList.add(PERMISSION[0])
        if (!isWriteStorageEnabled) requestPermisstionList.add(PERMISSION[1])
        if (!isLocationAccessEnabled) requestPermisstionList.add(PERMISSION[2])

        //説明不要な場合
        if (!isNeedExplainForPermission){
            ActivityCompat.requestPermissions(
                this@EditActivity,
                        requestPermisstionList.toArray(arrayOfNulls(requestPermisstionList.size)),
                        RQ_CODE_PERMISSION
            )
            return
        }

        //説明が必要な場合
        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle(getString(R.string.permission_request_title))
            setMessage(getString(R.string.permission_request_message))
            setPositiveButton(getString(R.string.admit)){ dialog: DialogInterface?, which: Int ->
                ActivityCompat.requestPermissions(
                    this@EditActivity,
                    requestPermisstionList.toArray(arrayOfNulls(requestPermisstionList.size)),
                    RQ_CODE_PERMISSION
                )
            }
            setNegativeButton(getString(R.string.reject)){dialog: DialogInterface?, which: Int ->
                Toast.makeText(this@EditActivity,getString(R.string.cannot_go_any_further),Toast.LENGTH_SHORT).show()
                finish()
            }
            show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode != RQ_CODE_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if(grantResults.size <= 0) return

        for ( i in 0.. permissions.size -1){
            when(permissions[i]){
                PERMISSION[0] ->{
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this@EditActivity,getString(R.string.cannot_go_any_further),
                            Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    isCameraEnabled = true
                }
                PERMISSION[1] ->{
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this@EditActivity,getString(R.string.cannot_go_any_further),
                            Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    isWriteStorageEnabled = true
                }
                PERMISSION[2] ->{
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this@EditActivity,getString(R.string.cannot_go_any_further),
                            Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    isLocationAccessEnabled = true
                }
            }
        }
        if (isCameraEnabled && isWriteStorageEnabled && isLocationAccessEnabled) launchCameta() else finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply {
            findItem(R.id.action_settings).isVisible = true  //セッティング
            findItem(R.id.action_share).isVisible = false //シェア
            findItem(R.id.action_comment).isVisible = false //テロップ
            findItem(R.id.action_delete).isVisible = true //削除
            findItem(R.id.action_edit).isVisible = false //編集
            findItem(R.id.action_cameta).isVisible  = if (mode == ModeInEdit.SHOOT) true else false //カメラ
        }
        return true

    }

    private fun launchCameta() {
        Log.d("loging","パーミッションエントリー")

    }

}
