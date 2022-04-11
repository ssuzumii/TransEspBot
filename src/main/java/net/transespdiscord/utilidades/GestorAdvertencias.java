package net.transespdiscord.utilidades;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.crud.AdvertenciaCRUD;
import net.transespdiscord.entidades.Advertencia;
import net.transespdiscord.enums.IdCanales;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GestorAdvertencias {
    public static void programarTarea(Advertencia advertencia) {
        if (advertencia.getUnixBaja() != null) { // ¡La fecha de baja podría ser nula! Programar solo si no es nula.
            final ScheduledExecutorService vigilante = Executors.newSingleThreadScheduledExecutor();
            long segundos = advertencia.getUnixBaja() - Instant.now().getEpochSecond();

            advertencia.setTarea(vigilante.schedule(() -> bajaAdvertencia(advertencia), segundos, TimeUnit.SECONDS));
        }
    }

    public static ArrayList<Advertencia> cargarLista() {
        ArrayList<Advertencia> advertencias = AdvertenciaCRUD.listar();

        for (Advertencia a : advertencias) {
            programarTarea(a);
        }

        return advertencias;
    }

    public static void cancelarTarea(Advertencia a) {
        a.getTarea().cancel(false);
    }

    public static void bajaAdvertencia(Advertencia advertencia) {
        JDA jda = TransEspBot.jda;
        Guild servidor = jda.getGuildById(IdCanales.SERVIDOR_TRANS_ESP.id);
        TextChannel logs = servidor.getTextChannelById(IdCanales.LOGS.id);

        User usuarie = jda.retrieveUserById(advertencia.getIdUsuarie()).complete();
        User responsable = jda.retrieveUserById(advertencia.getIdResponsable()).complete();
        String motivo = advertencia.getMotivo();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Baja de advertencia #" + advertencia.getId())
                .setColor(Color.cyan)
                .addField("Usuarie:", usuarie.getAsMention() + "\n" + usuarie.getAsTag(), true)
                .addField("Responsable:", responsable.getAsMention() + "\n" + responsable.getAsTag(), true)
                .addField("Motivo:", motivo, false)
                .setTimestamp(Instant.now());

        if (advertencia.getUnixBaja() != null) {
            eb.addField("Expira:", "<t:" + advertencia.getUnixBaja() + ":F>", false);
        }

        logs.sendMessageEmbeds(eb.build()).queue();
        log.info("Advertencia #" + advertencia.getId() + " eliminada para " + usuarie.getAsTag() + ".");

        // Envío de MD
        if (servidor.retrieveMember(usuarie).complete() != null) {
            usuarie.openPrivateChannel().queue((canalPrivado) -> canalPrivado.sendMessageEmbeds(eb.build()).queue());
        }

        if (advertencia.getUnixBaja() != null) {
            GestorAdvertencias.cancelarTarea(advertencia);
        }

        AdvertenciaCRUD.eliminar(advertencia);
    }

    public static int comprobarSanciones(User usuarie) {
        List<Advertencia> advertencias = AdvertenciaCRUD.obtenerPorIdUsuarie(usuarie.getId());

        if (advertencias.size() >= 5) {
            try {
                usuarie.getJDA().getGuilds().get(0).getMember(usuarie).ban(0).queue();
                return 1; // BAN
            } catch (NullPointerException e) {
                return -1; // ERROR -> NO ESTÁ EN SERVIDOR
            } catch (HierarchyException e) {
                return -2; // ERROR -> IMPOSIBLE BANEAR POR JERARQUÍA DE ROLES
            }
        } else {
            return 0; // NO DEBE BANEARSE
        }
    }
}
