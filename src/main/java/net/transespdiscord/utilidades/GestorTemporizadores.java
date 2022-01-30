package net.transespdiscord.utilidades;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.crud.TemporizadorActivoCRUD;
import net.transespdiscord.entidades.TemporizadorActivo;
import net.transespdiscord.enums.IdCanales;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GestorTemporizadores {
    public static void programarTarea(TemporizadorActivo temporizador) {
        JDA jda = TransEspBot.jda;
        final ScheduledExecutorService vigilante = Executors.newSingleThreadScheduledExecutor();

        long segundos = temporizador.getTiempoUnix() - Instant.now().getEpochSecond();

        temporizador.setTarea(vigilante.schedule(() -> {
            Guild servidor = jda.getGuildById(IdCanales.SERVIDOR_TRANS_ESP.id);

            try {
                servidor.retrieveMemberById(temporizador.getIdSolicitante()).queue(miembre -> {
                    miembre.getUser().openPrivateChannel().queue((canalPrivado) -> {
                        canalPrivado.sendMessage("***RECORDATORIO:*** Acaba de saltar la alarma \""
                                + temporizador.getNombre() + "\" que habías establecido en Trans en Español.").queue();
                    });

                    TransEspBot.temporizadores.remove(temporizador);
                    GestorTemporizadores.cancelarTarea(temporizador);
                    TemporizadorActivoCRUD.eliminar(temporizador);
                });
            } catch (ErrorResponseException | NullPointerException e) {
                e.printStackTrace();
            }

        }, segundos, TimeUnit.SECONDS));
    }

    public static ArrayList<TemporizadorActivo> cargarLista() {
        ArrayList<TemporizadorActivo> temporizadores = new ArrayList<>();
        List<TemporizadorActivo> temporizadoresBD = TemporizadorActivoCRUD.listar();

        for (TemporizadorActivo t : temporizadoresBD) {
            programarTarea(t);
            temporizadores.add(t);
        }

        return temporizadores;
    }

    public static void cancelarTarea(TemporizadorActivo t){
        t.getTarea().cancel(false);
    }
}
