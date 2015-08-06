package com.example.reader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ArrayAdapter<String> mListOfCategoriesAdapter;

    public CategoriesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indica que esse fragmento possui um menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Já que possui um menu "setHasOptionsMenu(true)", inflamos esse menu
        inflater.inflate(R.menu.categories_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Achamos o id do item selecionado
        int id = item.getItemId();

        // Se o id for "action_refresh", instanciamos nosssa tarefa assíncrona
        // Depois executamos essa tarefa
        if (id == R.id.action_refresh) {
            FetchReaderAPITask apiTask = new FetchReaderAPITask();
            apiTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Novos dados iniciais
        String[] data = {
                "Atualize os dados..."
        };

        // Criando uma lista (ArrayList) com os dados criados acima
        List<String> listOfLastPosts = new ArrayList<String>(Arrays.asList(data));

        // Agora que já temos os dados, vamos criar um Adapter, no caso um ArrayAdapter

        mListOfCategoriesAdapter = new ArrayAdapter<String>(
                getActivity(), // O contexto atual
                R.layout.list_item_last_posts, // O arquivo de layout de cada item
                R.id.list_item_post_title_textview, // O ID do campo a ser preenchido
                listOfLastPosts // A fonte dos dados
        );

        // Inflamos o layout principal
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Cria uma referência para a ListView
        ListView listView = (ListView) rootView.findViewById(R.id.list_last_posts);
        listView.setAdapter(mListOfCategoriesAdapter);

        // Retornamos tudo
        return rootView;
    }

    public class FetchReaderAPITask extends AsyncTask<Void, Void, String[]> {
        private final String LOG_TAG = FetchReaderAPITask.class.getSimpleName();

        private String[] getCategoriesDataFromJson(String readerApiJsonStr)
                throws JSONException {

            // Lista de "nós" do JSON que vamos ler
            // Status:
            final String API_STATUS = "status";
            // Se o status for ERRO, temos um "erro" com o código e a mensagem do erro:
            final String API_ERROR = "erro";
            final String API_ERROR_CODE = "code";
            final String API_ERROR_MESSAGE = "message";
            // Se o status for OK, temos uma "response" com o endpoint usado
            final String API_RESPONSE = "response";
            final String API_RESPONSE_COUNT = "count";

            // Por enquanto só estamos requisitando o endpoint "categorias"
            final String API_ENDPOINT_CATEGORIAS = "categorias";

            // Cada categoria possui id, nome e count, mas ignoraremos o ID por enquanto
            final String API_ENDPOINT_CATEGORIAS_NOME = "nome";
            final String API_ENDPOINT_CATEGORIAS_COUNT = "count";

            // Instanciamos o JSONObject
            JSONObject readerApiJson = new JSONObject(readerApiJsonStr);

            // Se o status for "ERROR"
            if (readerApiJson.getString(API_STATUS).equals("ERROR")) {
                String erroCode = readerApiJson.getJSONObject(API_ERROR).getString(API_ERROR_CODE);
                String erroMessage = readerApiJson.getJSONObject(API_ERROR).getString(API_ERROR_MESSAGE);

                // Escreve um log com o formato "(999) Mensagem"
                // Veja a lista de erros em: http://api.jangal.com.br/reader/
                Log.e(LOG_TAG, "Erro na API: (" + erroCode + ") " + erroMessage);
                return null;
            }

            // Se o status for "OK"
            if (readerApiJson.getString(API_STATUS).equals("OK")) {
                // Pegamos o Objeto "response"
                JSONObject response = readerApiJson.getJSONObject(API_RESPONSE);

                // Do Objeto response, pegamos o int count e a array de itens
                int countCategories = response.getInt(API_RESPONSE_COUNT);
                JSONArray categoriesArray = response.getJSONArray(API_ENDPOINT_CATEGORIAS);

                // Criamos uma String[] para armazenar cada linha que iremos passar para a View
                String[] result = new String[countCategories];

                // Fazemos um laço para percorrer os itens da Array
                for(int i = 0; i < categoriesArray.length(); i++) {
                    // Pegamos o Obj
                    JSONObject categoria = categoriesArray.getJSONObject(i);

                    // Lemos o nome e o count dessa categoria
                    String nome = categoria.getString(API_ENDPOINT_CATEGORIAS_NOME);
                    String count = categoria.getString(API_ENDPOINT_CATEGORIAS_COUNT);

                    // Como ´s tudo string, concatenamos no formado "Categoria (99)"
                    result[i] = nome + " (" + count + ")";
                }

                return result;
            }

            // Se por algum motivo não tínhamos nem um status "OK", nem um "ERROR"
            // Então escrevemos o Log e retornamos null
            Log.e(LOG_TAG, "Erro ao ler o status da resposta");
            return null;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
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
                    // Se a resposta for null, retornamos null
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Como o buffer está vazio, retornamos null
                    return null;
                }

                // Atribui o buffer como string à variável que armazena a resposta
                responseJson = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Erro: ", e);
                // Como houve uma excessão, retornamos null
                return null;
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

            //Já que "getCategoriesDataFromJson" é throws, usaremos um try/catch
            try {
                return getCategoriesDataFromJson(responseJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Se o retonorno não for nulo
            if (result != null) {
                mListOfCategoriesAdapter.clear();
                for (String categoryStr : result) {
                    mListOfCategoriesAdapter.add(categoryStr);
                }
            }
        }
    }
}
