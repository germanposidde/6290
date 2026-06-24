package com.kmp.pyr.legal

/**
 * Platform legal configuration. Android shows only the Privacy Policy; iOS shows
 * both Privacy Policy and Terms of Use, and the policy URLs differ per platform.
 */
expect object Legal {
    val isIos: Boolean
    val privacyPolicyUrl: String
    val termsOfUseUrl: String?
}
