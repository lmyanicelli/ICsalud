package com.luciayanicelli.icsalud.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by LuciaYanicelli on 14/3/2017.
 *
 * ESTA CLASE ASÍNCRONA ENVÍA UN CORREO ELECTRÓNICO A LOS MAILS INDICADOS EN CONFIGURACIÓN (PERSONAL DE SALUD)
 * EL TEXTO A ENVIAR SE INDICA EN EL CONSTRUCTOR, JUNTO CON EL ASUNTO DEL MAIL
 *
 * POR EL MOMENTO PARA UTILIZAR JAVAMAIL SIN QUE EL USUARIO TENGA QUE INTERACTUAR, SE UTILIZA UN MAIL ESPECÍFICO QUE TIENE HABILITADAS LAS
 * OPCIONES DE SEGURIDAD PARA PODER UTILIZAR JAVAMAIL
 * ---
 * EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(asunto, textoEnviar);
 enviarMailSegundoPlano.execute();
 ---

 */

public class EnviarMailSegundoPlano extends AsyncTask<Void, Void, Boolean> {

    private String textoEnviar, asunto, contactos;
    private Context context;

  //  private String emailRemitentes; //HAY QUE OBTENERLO DE LA BD DE CONFIGURACION DATOS CONTACTOS


  //Mail habilitado para utilizar javamail - opciones de seguridad
    private String javamail_mail_autorizado;
    private String javamail_contraseña_mail_autorizado;


    private Session session;


    private String nombrePaciente, emailPaciente;

    private Boolean msjEnviado = null;


    /*
    @param textoEnviar : String Texto que se enviará por mail a los remitentes indicados en contactos
    @param asunto: String Asunto del javamail_mail_autorizado electrónico
    @param textoEnviar: String Texto que se enviará por mail a los remitentes indicados en contactos
    @param contactos: String Contactos emails a quienes se les enviará el correo electrónico
     */

    public EnviarMailSegundoPlano(@NonNull Context context, @NonNull String asunto, @NonNull String textoEnviar, @NonNull String contactos) {

        this.context = context;
        this.asunto = asunto;
        this.textoEnviar = textoEnviar;
        this.contactos = contactos;
    }

    @Override
        protected void onProgressUpdate(Void... values) {

        }

    @Override
    protected Boolean doInBackground(Void... strings) {


        //TAREA PRINCIPAL
        try {

            //INICIALIZO CONFIGURACIONES
            Configuraciones configuraciones = new Configuraciones(context);

       //     emailRemitentes = configuraciones.getUserEmailRemitente();
        //    String[] emailArray = emailRemitentes.split(",");

            nombrePaciente = configuraciones.getUserSurname() +  ", " + configuraciones.getUserName();
            emailPaciente = configuraciones.getUserEmail();

            javamail_mail_autorizado = configuraciones.getJAVAMAILmailAutorizado();



            javamail_contraseña_mail_autorizado = configuraciones.getJAVAMAILcontraseñaMailAutorizado();

            //

            StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.googlemail.com");
            properties.put("mail.smtp.socketFactory.port","465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth","true");
            properties.put("mail.smtp.port", "465");

            try {

             /*   session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(javamail_mail_autorizado, javamail_contraseña_mail_autorizado);
                    }
                });
                */
                    //https://javaee.github.io/javamail/FAQ#getdefaultinstance
                session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(javamail_mail_autorizado, javamail_contraseña_mail_autorizado);
                    }
                });

                if(session!=null){
                 //   for(int x=0; x<emailArray.length; x++){
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(javamail_mail_autorizado));
                     //   message.setSubject(asunto + " - " + nombrePaciente);
                        message.setSubject(nombrePaciente + " - " + emailPaciente + " - " + asunto);
                       // message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(emailArray[x]));
                        //
                        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(contactos));
                        //
                        message.setContent(textoEnviar,"text/html; charset=utf-8");
                        try {
                            Transport.send(message);
                            msjEnviado = true;
                        } catch (Exception e) {
                    /* El javamail_mail_autorizado no se envió */
                          //  msjEnviado = false;
                           msjEnviado = reintentar(session, javamail_mail_autorizado,nombrePaciente + " - " + emailPaciente + " - " + asunto, contactos, textoEnviar);
                        }
                 //   }


                }


            }catch (Exception e){
                e.printStackTrace();
                msjEnviado = false;
            }

        } catch (Exception e) {
                e.printStackTrace();
                msjEnviado = false;
        }

        return msjEnviado;

    }

    private Boolean reintentar(Session session, String javamail_mail_autorizado, String subject, String email, String textoEnviar) throws MessagingException {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(javamail_mail_autorizado));
        message.setSubject(subject);
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(email));
        message.setContent(textoEnviar,"text/html; charset=utf-8");
        try {
            Transport.send(message);
            msjEnviado = true;
        } catch (Exception e) {
                    /* El javamail_mail_autorizado no se envió */
            msjEnviado = false;
           // reintentar(session, javamail_mail_autorizado,asunto + " - " + nombrePaciente,emailArray[x], textoEnviar);
        }

        return  msjEnviado;

    }


    @Override
        protected void onPreExecute() {
    }



        @Override
        protected void onCancelled() {

        }

 /*   @Override
    protected void onPostExecute(Boolean result) {

        this.msjEnviado = result;

    }
*/

  /*  public Boolean getResult() {
        return result;
    }
    */
}
