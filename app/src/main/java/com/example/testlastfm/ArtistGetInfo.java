package com.example.testlastfm;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.example.testlastfm.MainActivity.API_KEY;
import static com.example.testlastfm.MainActivity.TAG;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArtistGetInfo extends Fragment implements HttpPoster.JSONResponse {
    String request = "";

    SearchView svSearch;
    ImageButton btnSearch;
    ImageView ivImgArtist;
    TextView tvNameArtist,
            tvBiography,
            btnComeBack;


    public ArtistGetInfo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_get_info, container, false);
        svSearch = view.findViewById(R.id.svSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        ivImgArtist = view.findViewById(R.id.ivImgArtist);
        tvNameArtist = view.findViewById(R.id.tvNameArtist);
        tvBiography = view.findViewById(R.id.tvBiography);
        btnComeBack = view.findViewById(R.id.btnComeBack);

        //Т.к. переход длится 1 сек, то до окончания перехода запрещу нажатие на кнопку(Приводило к сбоям при быстрой навигации)
        btnComeBack.setEnabled(false);
        new Handler().postDelayed(() -> {
            btnComeBack.setEnabled(true);
        },1000);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sendQuery();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnSearch.setOnClickListener(v -> {
            sendQuery();
        });

        btnComeBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                v.setEnabled(false);
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_nav_graph);
                navController.popBackStack();
            }
        });

        return view;
    }

    public void sendQuery() {
        //Получаю имя артиста
        String artistName = svSearch.getQuery().toString();
        //конвертирую запрос в формат UTF-8
        artistName = MainActivity.stringToUTF8(artistName);
        String method = "artist.getinfo";
        //Создаю запрос получения информации об артисте
        request = "?method=" + method
                + "&artist=" + artistName
                + "&api_key=" + API_KEY
                + "&format=json";

        //https://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=lil&api_key=734df4355aeea4ec7802cec249500e74
        //Отправляю запрос в UTF-8 формате
        new HttpPoster(this, method).execute("https://ws.audioscrobbler.com/2.0/" + request);
    }

    public void artistGetInfo(String result) {
        //Log.d(TAG, result);
        if (result != null)
            if (result.length() > 0)
                try {
                    //Получу ответ в формате JSON
                    JSONObject jsonResponse = new JSONObject(result);
                    //Получу объект JSON артиста
                    JSONObject artist = jsonResponse.getJSONObject("artist");
                    //Получу массив объектов JSON с картинками
                    JSONArray jsonImgArray = artist.getJSONArray("image");
                    //Получу объект JSON биографии
                    JSONObject bio = artist.getJSONObject("bio");

                    //Log.d(TAG, jsonResponse.toString());

                    //Найду ссылку на картинку с размером size
                    String size = "medium";//Размер картинки по умолчанию
                    if (getContext() != null)
                        size = MainActivity.displayDensity(getContext());//Получаю нужный размер картинки в соответствии с плотностью дисплея

                    String imgUrl = "";
                    for (int i = 0; i < jsonImgArray.length(); i++) {
                        JSONObject imgObject = jsonImgArray.getJSONObject(i);
                        if (imgObject.getString("size").equals(size)) {
                            imgUrl = imgObject.getString("#text");
                            break;
                        }
                    }

                    String artistName = artist.getString("name");
                    String biography = bio.getString("summary");
                    biography = deleteLink(biography);
                    biography = biography.trim();

                    new DownloadImageTask(ivImgArtist).execute(imgUrl);
                    tvNameArtist.setText(artistName);
                    tvBiography.setText(biography);

                } catch (JSONException e) {
                    //Здесь может быть вывод следующей ошибки в формате JSON:
                    //{"error":6,"message":"The artist you supplied could not be found","links":[]}
                    Log.d(TAG, "artistGetInfo Error: " + result);
                    e.printStackTrace();
                }
    }

    public String deleteLink(String str) {
        int endPos = str.indexOf("<a href=");

        if (endPos > 0)
            str = str.substring(0, endPos);

        return str;
    }

    @Override
    public void onGetResponse(String method, String JSON) {
        if (method.equals("artist.getinfo"))
            artistGetInfo(JSON);
    }
}