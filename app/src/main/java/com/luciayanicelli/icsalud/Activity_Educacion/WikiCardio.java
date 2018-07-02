package com.luciayanicelli.icsalud.Activity_Educacion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.luciayanicelli.icsalud.R;


public class WikiCardio extends Fragment {


    //Vista


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wiki_cardio, container, false);

        //Web Page - wiki cardio
        WebView myWebView = (WebView) view.findViewById(R.id.webview);
        myWebView.loadUrl("http://www.wikicardio.org.ar/wiki/P%C3%A1gina_principal");

        //Enabling JavaScript
    //    WebSettings webSettings = myWebView.getSettings();
    //    webSettings.setJavaScriptEnabled(true);

        //Actualizar vista
        refreshView();


        return view;
    }

    //EN LUGAR DE HACER ESTO, CONVENDRÍA TENER UN FRAGMENT XA CARGAR LOS DATOS Y OTRO  QUE SÓLO CONTENGA EL TEXT VIEW INDICANDO QUE YA CARGÓ LOS DATOS
    //eL FRAGMENT CON EL TEXTVIEW SERVIRÍA XA TODOS LOS TABS
    private void refreshView() {
    }

}
