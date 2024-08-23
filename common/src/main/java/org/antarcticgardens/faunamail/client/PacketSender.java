package org.antarcticgardens.faunamail.client;

public abstract class PacketSender {

    public static PacketSender implementation;

    public static void sendSealPacket(String[] text, String address, String player) {
        implementation.sendSealPacket_(text, address, player);
    }

    public static void sendUnsealPacket() {
        implementation.sendUnsealPacket_();
    }

    public static void mail(int entityId) {
        implementation.mail_(entityId);
    }

    public abstract void sendUnsealPacket_();

    public abstract void sendSealPacket_(String[] text, String address, String player);

    public abstract void mail_(int entityId);
}
