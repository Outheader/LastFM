package com.example.testlastfm;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FindMusicArtists extends Fragment {
    final static int NAV_BIOGRAPHY_OF_ARTIST_PAGE = R.id.action_findMusicArtists_to_artistGetInfo;
    final static int NAV_THE_BEST_TRACK_PAGE = R.id.action_findMusicArtists_to_artistGetTopTracks;

    ImageButton btnBiographyOfArtist,
            btnTheBestTracks;

    NavController navController;

    public FindMusicArtists() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_music_artists, container, false);
        btnBiographyOfArtist = view.findViewById(R.id.btnBiographyOfArtist);
        btnTheBestTracks = view.findViewById(R.id.btnTheBestTracks);

        //Т.к. переход длится 1 сек(если нажать Вернуться назад), то до окончания перехода запрещу нажатие на кнопку(Приводило к сбоям при быстрой навигации)
        btnBiographyOfArtist.setEnabled(false);
        btnTheBestTracks.setEnabled(false);
        new Handler().postDelayed(() -> {
            btnBiographyOfArtist.setEnabled(true);
            btnTheBestTracks.setEnabled(true);
        },1000);

        btnBiographyOfArtist.setOnClickListener(v -> {
            //Запрещаю клики по кнопкам (чтобы не кликнуть несколько раз)
            v.setEnabled(false);
            btnTheBestTracks.setEnabled(false);
            //Перейду на страницу по поиску артиста
            navigateTo(NAV_BIOGRAPHY_OF_ARTIST_PAGE);
        });

        btnTheBestTracks.setOnClickListener(v -> {
            //Запрещаю клики по кнопкам (чтобы не кликнуть несколько раз)
            v.setEnabled(false);
            btnBiographyOfArtist.setEnabled(false);
            //Перейду на страницу лучших треков артиста
            navigateTo(NAV_THE_BEST_TRACK_PAGE);
        });

        return view;
    }

    public void navigateTo(@IdRes int navigationTo){
        if (getActivity() != null) {
            navController = Navigation.findNavController(getActivity(), R.id.fragment_nav_graph);
            NavDestination navDestination = navController.getCurrentDestination();
            if (navDestination != null)
                if (navDestination.getId() == R.id.findMusicArtists)
                    navController.navigate(navigationTo);
        }
    }


}