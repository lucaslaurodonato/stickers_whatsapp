package br.com.stickers.presentation.all

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import br.com.stickers.R
import br.com.stickers.data.local.SharedPref
import br.com.stickers.mechanism.AppUtils
import br.com.stickers.presentation.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_sticker_pack_details.toolbar
import kotlinx.android.synthetic.main.activity_sticker_pack_info.*
import kotlinx.android.synthetic.main.include_toolbar.view.*

class StickerPackInfoActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, StickerPackInfoActivity::class.java)
    }

    private var website: String? = null
    private var email: String? = null
    private var privacyPolicy: String? = null
    private var licenseAgreement: String? = null
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(applicationContext)
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_sticker_pack_info)
        receiveDataToSetup()
        setupToolbar()
        setupItems()
        appVersion()
        darkMode()
    }

    private fun darkMode() {
        if (sharedPref.loadNightModeState()) {
            dark_mode.isChecked = true
        }

        dark_mode.setOnCheckedChangeListener { _, b ->
            if (b) {
                sharedPref.setNightModeState(true)
                restartApplication()
            } else {
                sharedPref.setNightModeState(false)
                restartApplication()
            }
        }
    }

    private fun restartApplication() {
        Intent(applicationContext, EntryActivity::class.java).let {
            startActivity(it)
            finish()
        }
    }

    private fun appVersion() {
        app_version.text =
            getString(R.string.app_version_settings, AppUtils.version(applicationContext))
    }

    private fun receiveDataToSetup() {
        website = intent.getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_WEBSITE)
        email = intent.getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_EMAIL)
        privacyPolicy =
            intent.getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_PRIVACY_POLICY)
        licenseAgreement =
            intent.getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_LICENSE_AGREEMENT)
    }

    private fun setupItems() {
        if (website != null) {
            setupTextView(website!!, R.id.view_webpage)
        }
        if (TextUtils.isEmpty(email)) {
            send_email.visibility = GONE
        } else {
            send_email.setOnClickListener { v: View? -> launchEmailClient(email!!) }
        }
        if (privacyPolicy != null) {
            setupTextView(privacyPolicy!!, R.id.privacy_policy)
        }
        if (licenseAgreement != null) {
            setupTextView(
                licenseAgreement!!,
                R.id.license_agreement
            )
        }
    }

    private fun setupToolbar() {
        toolbar.apply {
            toolbar_title.text = getString(R.string.title_activity_sticker_pack_info)
            info.visibility = GONE
            back.setOnClickListener {
                finish()
            }
        }
    }

    private fun setupTextView(website: String, @IdRes textViewResId: Int) {
        val viewWebPage = findViewById<TextView>(textViewResId)
        if (TextUtils.isEmpty(website)) {
            viewWebPage.visibility = GONE
        } else {
            viewWebPage.setOnClickListener { v: View? -> launchWebPage(website) }
        }
    }

    private fun launchEmailClient(email: String) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        startActivity(
            Intent.createChooser(
                emailIntent,
                resources.getString(R.string.info_send_email_to_prompt)
            )
        )
    }

    private fun launchWebPage(website: String) {
        val uri = Uri.parse(website)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}