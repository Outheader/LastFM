package com.example.testlastfm;

import static com.example.testlastfm.MainActivity.API_KEY;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.testlastfm.MainActivity.TAG;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ArtistGetTopTracks extends Fragment implements HttpPoster.JSONResponse, TopTracksAdapter.Events {
    final int LIMIT_TRACKS = 3;//Количество отображаемых треков
    final int TOP_10 = 10;//Буду рандомно отображать треки из 10ки лучших
    TopTracksAdapter topTracksAdapter;
    TopTracksAdapter.HolderBody body;
    List<TopTracksAdapter.HolderBody> bodyList;
    String request = "";

    SearchView svSearch;
    ImageButton btnSearch;
    RecyclerView recyclerView;
    TextView btnComeBack;

    public ArtistGetTopTracks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_get_top_tracks, container, false);
        svSearch = view.findViewById(R.id.svSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        recyclerView = view.findViewById(R.id.recyclerView);
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
        String method = "artist.gettoptracks";
        //Создаю запрос получения информации об артисте
        request = "?method=" + method
                + "&artist=" + artistName
                + "&api_key=" + API_KEY
                + "&format=json";

        Log.d(TAG, "https://ws.audioscrobbler.com/2.0/" + request);
        //Отправляю запрос в UTF-8 формате
        new HttpPoster(this, method).execute("https://ws.audioscrobbler.com/2.0/" + request);
    }

    public void artistGetTopTracks(String result) {
        if (result != null)
            if (result.length() > 0)
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject topTracks = jsonResponse.getJSONObject("toptracks");
                    JSONArray tracks = topTracks.getJSONArray("track");

                    //Получаю уникальные номера треков
                    int[] numberTracks = randomSeed(LIMIT_TRACKS);
                    bodyList = new ArrayList<>();

                    for (int i = 0; i < LIMIT_TRACKS; i++) {
                        //Получаю объект на песню
                        JSONObject track = tracks.getJSONObject(numberTracks[i]);
                        //Получаю у объекта песни массив с картинками
                        JSONArray jsonArrayImg = track.getJSONArray("image");
                        //Получаю название песни
                        String titleTrack = track.getString("name");

                        //Найду ссылку на картинку с размером size
                        String size = "medium";//Размер картинки по умолчанию
                        if (getContext() != null)
                            //Получаю нужный размер картинки в соответствии с плотностью дисплея
                            //size будет одним из значений: "small", "medium", "large", "extralarge", "mega"
                            size = MainActivity.displayDensity(getContext());
                        //Получаю из массива ссылку на картинку нужной размерности
                        String imgUrl = "";
                        for (int j = 0; j < jsonArrayImg.length(); j++) {
                            JSONObject imgObject = jsonArrayImg.getJSONObject(j);
                            if (imgObject.getString("size").equals(size)) {
                                imgUrl = imgObject.getString("#text");
                                break;
                            }
                        }

                        //Добавляю трек в список
                        body = new TopTracksAdapter.HolderBody();
                        body.setImgAlbum(imgUrl);
                        body.setNameTrack(titleTrack);
                        bodyList.add(body);

                        Log.d(TAG, "URL: " + imgUrl);
                        Log.d(TAG, "TITLE: " + titleTrack);
                    }

                    //Инициализирую адаптер
                    topTracksAdapter = new TopTracksAdapter(this, bodyList);
                    recyclerView.setAdapter(topTracksAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
    }

    public int[] randomSeed(int maxValue) {
        int[] randoms = new int[maxValue];

        Random random = new Random();

        int n = 0;
        boolean repeat;
        for (int i = 0; i < LIMIT_TRACKS; i++) {
            repeat = true;
            while (repeat) {
                repeat = false;
                n = random.nextInt(TOP_10);
                for (int j = 0; j < i; j++)
                    if (n == randoms[j]) {
                        repeat = true;
                        break;
                    }
            }
            randoms[i] = n;
        }
        return randoms;
    }

    @Override
    public void onGetResponse(String method, String JSON) {
        if (method.equals("artist.gettoptracks"))
            artistGetTopTracks(JSON);
    }

    @Override
    public void onItemClick(int position) {
        position++;
        Log.d(TAG, "Выбрал трек №" + position);
    }
}