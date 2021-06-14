package com.example.bioSpecies.ui.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.RankingNrCapturas
import com.example.bioSpecies.domain.auth.LoginFragment
import com.example.bioSpecies.domain.auth.RegisterFragment
import com.example.bioSpecies.ui.fragments.*

class NavigationManager {

    companion object{

        private fun placeFragment(fm: FragmentManager, fragment: Fragment) {
            Log.i("navigation", "navigation")
            val transition = fm.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }

        private fun placeChildFragment(fm: FragmentManager, fragment: Fragment) {
            Log.i("navigation", "navigation")
            val transition = fm.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }

        fun goToMainScreen(fm: FragmentManager) {
            placeFragment(
                fm,
                MapFragment()
            )
        }

        fun goToProfile(fm: FragmentManager) {
            placeFragment(
                    fm,
                    ProfileFragment()
            )
        }

        fun goToCaderneta(fm: FragmentManager) {
            placeFragment(
                    fm,
                    CadernetaFragment()
            )
        }

        fun goToCadenetaLista(fm: FragmentManager) {
            placeFragment(
                    fm,
                    CadernetaListaFragment()
            )
        }

        fun goToPerfilEspecies(fm: FragmentManager){
            placeFragment(
                    fm,
                    EspeciePerfilFragment()
            )
        }

        fun goToChallenges(fm: FragmentManager) {
            placeFragment(
                    fm,
                    ChallengesFragment()
            )
        }

        fun goToEvaluation(fm: FragmentManager) {
            placeFragment(
                    fm,
                    EvaluationFragment()
            )
        }

        fun goToEvaluationNoName(fm: FragmentManager) {
            placeFragment(
                fm,
                PhotosWithNoNameFragment()
            )
        }

        fun goToEvaluationNoNameGame(fm: FragmentManager) {
            placeFragment(
                    fm,
                    PhotosWithNoNameGameFragment()
            )
        }


        fun goToAbout(fm: FragmentManager) {
            placeFragment(
                fm,
                AboutFragment()
            )
        }


        fun goToSettings(fm: FragmentManager) {
            placeFragment(
                fm,
                SettingsFragment()
            )
        }

        fun goToCamera(fm: FragmentManager) {
            placeFragment(
                fm,
                CameraFragment()
            )
        }

        fun goToRanking(fm: FragmentManager) {
            placeFragment(
                    fm,
                    RankingListaFragment()
            )
        }

        fun goToRankingNrCaptures(fm: FragmentManager) {
            placeFragment(
                    fm,
                    RankingNrCapturasFragment()
            )
        }

        fun goToEditProfile(fm: FragmentManager) {
            placeFragment(
                    fm,
                    EditProfileFragment()
            )
        }

        fun goToStatisticsProfile(fm: FragmentManager) {
            placeFragment(
                    fm,
                    StatisticsProfileFragment()
            )
        }

        fun goToCaptureHistory(fm: FragmentManager){
            placeFragment(
                fm,
                CapturesHistoryFragment()
            )
        }

        fun goToFaq(fm: FragmentManager){
            placeFragment(
                    fm,
                    FaqFragment()
            )
        }

        private fun placeFragmentInLoginActivity(fm: FragmentManager, fragment: Fragment) {
            Log.i("navigation", "navigation")
            val transition = fm.beginTransaction()
            transition.replace(R.id.activity_login, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }

        fun goToLogin(fm: FragmentManager){
            placeFragmentInLoginActivity(
                    fm,
                    LoginFragment()
            )
        }

        fun goToRegister(fm: FragmentManager){
            placeFragmentInLoginActivity(
                    fm,
                    RegisterFragment()
            )
        }


    }

}