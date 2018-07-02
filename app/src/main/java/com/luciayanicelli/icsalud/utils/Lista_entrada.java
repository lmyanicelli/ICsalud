package com.luciayanicelli.icsalud.utils;


/*OBJETO ENTRADA PARA EL LISTADO DE RECORDATORIOS*/


public class Lista_entrada {

        private String tipo;
        private String texto;
        private String _id;


        public Lista_entrada(String _id, String texto, String tipo) {
            this._id = _id;
            this.tipo = tipo;
            this.texto = texto;

        }


        public String get_texto() {
            return texto;
        }

        public String get_tipo() {
            return tipo;
        }

        public String get_id() { return _id; }

}
