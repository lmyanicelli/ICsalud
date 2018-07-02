package com.luciayanicelli.icsalud.utils;


/*OBJETO ENTRADA PARA EL LISTADO DE PROFESIONALES*/


public class Lista_entrada_profesionales {


        private String datos;
        private String profesional;
        private String _id;
        private String cel;
        private String email;


        public Lista_entrada_profesionales(String _id, String profesional, String datos) {
            this._id = _id;
            this.datos = datos;
            this.profesional = profesional;

        }


        public String get_id() { return _id; }
        public String getDatos() {      return datos;    }
        public String getProfesional() {       return profesional;    }


    public String getCel() {
        return cel;
    }

    public void setCel(String cel) {
        this.cel = cel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
