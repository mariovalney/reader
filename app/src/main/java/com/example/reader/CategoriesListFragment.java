package com.example.reader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Valney on 06/08/2015.
 */
public class CategoriesListFragment extends Fragment {
    private final String LOG_TAG = CategoriesListFragment.class.getSimpleName();

    public CategoriesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Criando uma array falsa de strings com os títulos das nossas Aulas
        String[] data = {
                "Aula 1: Getting Start!",
                "Aula 2: Como instalar o Android Studio?",
                "Aula 3: Como criar um projeto no Android Studio?",
                "Aula 4: O que é Activity e Fragment?",
                "Aula 5: Como criar a User Interface (UI) do meu Aplicativo Android?"
        };

        // Criando uma lista (ArrayList) com os dados criados acima
        List<String> listOfLastPosts = new ArrayList<String>(Arrays.asList(data));

        // Agora que já temos os dados, vamos criar um Adapter, no caso um ArrayAdapter

        ArrayAdapter<String> listOfLastPostsAdapter = new ArrayAdapter<String>(
                getActivity(), // O contexto atual
                R.layout.list_item_last_posts, // O arquivo de layout de cada item
                R.id.list_item_post_title_textview, // O ID do campo a ser preenchido
                listOfLastPosts // A fonte dos dados
        );

        // Inflamos o layout principal
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Cria uma referência para a ListView
        ListView listView = (ListView) rootView.findViewById(R.id.list_last_posts);
        listView.setAdapter(listOfLastPostsAdapter);

        // Retornamos tudo
        return rootView;
    }

    public class FetchReaderAPITask extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = FetchReaderAPITask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... voids) {
            // As variáveis da conexão devem ser declaradas fora do try/catch
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Irá conter a resposta como uma string
            String responseJson = null;

            try {
                // Constrói a URL a que iremos enviar o Request
                // Mais detalhes em: http://api.jangal.com.br/reader
                URL url = new URL("http://api.jangal.com.br/reader/v1/categorias");

                // Cria o Request (GET) e abre a conexão
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Lê a o input stream (entrada) como uma string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Se for nulo, mantemos a resposta como "null"
                    responseJson = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                if (buffer.length() == 0) {
                    // Como o buffer está vazio, a resposta também estará
                    responseJson = null;
                }

                // Atribui o buffer como string à variável que armazena a resposta
                responseJson = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Erro: ", e);
                // Como houve uma excessão, deixamos a resposta como null
                responseJson = null;
            } finally{
                if (urlConnection != null) {
                    // Se houve conexão, fechamos
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    // Se o reader não estiver nulo ainda, tentamos fechar
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        // Se houver excessão ao fechar, escrevemos um Log
                        Log.e(LOG_TAG, "Erro fechando o (BufferedReader) reader", e);
                    }
                }
            }
            return null;
        }
    }
}
