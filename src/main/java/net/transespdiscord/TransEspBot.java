package net.transespdiscord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.transespdiscord.entidades.TemporizadorActivo;
import net.transespdiscord.procesadores.ProcesadorEntradaServidor;
import net.transespdiscord.procesadores.ProcesadorSlash;
import net.transespdiscord.utilidades.GestorTemporizadores;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class TransEspBot {
    public static JDA jda = null;
    public static ArrayList<TemporizadorActivo> temporizadores = new ArrayList<>();

    public static void main(String[] args) {
        final String DISCORD_TOKEN = System.getenv("DISCORD_TOKEN");
        JDABuilder builder = JDABuilder.createDefault(DISCORD_TOKEN);

        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.addEventListeners(new ProcesadorSlash(), new ProcesadorEntradaServidor());
        builder.setActivity(Activity.playing("Trans en Español"));

        try {
            jda = builder.build().awaitReady();
        } catch (InterruptedException e) {
            System.err.println("ERROR: SE HA INTERRUMPIDO EL PROCESO. STACK TRACE:");
            e.printStackTrace();
            System.exit(1);
        } catch (LoginException e) {
            System.err.println("ERROR: TOKEN NO VALIDO. DEBE ESPECIFICARSE COMO UNA VARIABLE DE ENTORNO LLAMADA "
                    + "'DISCORD_TOKEN'.");
            System.exit(1);
        }

        // Si hemos llegado aquí ha ido bien
        System.out.println("JDA conectado!");

        // Carga de los comandos de Slash en el servidor para que así sean accesibles
        ProcesadorSlash.cargarComandos(jda);
        System.out.println("Comandos Slash cargados!");

        // Tareas programadas
        temporizadores = GestorTemporizadores.cargarLista();
        System.out.println("Temporizadores cargados desde BD!");

        ProcesadorEntradaServidor.programarTarea(jda);
        System.out.println("Tarea aviso a novates creada!");
    }
}
