package net.mehvahdjukaar.sleep_tight.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.sleep_tight.SleepTightClient;
import net.mehvahdjukaar.sleep_tight.SleepTightPlatformStuff;
import net.mehvahdjukaar.sleep_tight.core.PlayerSleepCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class ClientBoundSyncPlayerSleepCapMessage implements Message {
    @Nullable
    private final UUID id;
    private final long insomniaElapse;
    private final long sleepTime;
    private final int consecutiveNights;
    private final int homeBedNights;

    public ClientBoundSyncPlayerSleepCapMessage(FriendlyByteBuf buf) {
        if (buf.readBoolean()) this.id = buf.readUUID();
        else id = null;
        this.insomniaElapse = buf.readLong();
        this.sleepTime = buf.readLong();
        this.consecutiveNights = buf.readInt();
        this.homeBedNights = buf.readInt();
    }

    public ClientBoundSyncPlayerSleepCapMessage(PlayerSleepCapability c) {
        this.id = c.getHomeBed();
        this.insomniaElapse = c.getInsomniaWillElapseTimeStamp();
        this.sleepTime = c.getLastWokenUpTimeStamp();
        this.consecutiveNights = c.getConsecutiveNightsSlept();
        this.homeBedNights = c.getNightsSleptInHomeBed();
    }

    public ClientBoundSyncPlayerSleepCapMessage(Player player) {
        this(SleepTightPlatformStuff.getPlayerSleepCap(player));
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeBoolean(id != null);
        if (id != null) buf.writeUUID(id);
        buf.writeLong(insomniaElapse);
        buf.writeLong(sleepTime);
        buf.writeInt(consecutiveNights);
        buf.writeInt(homeBedNights);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Player p = SleepTightClient.getPlayer();
        var c = SleepTightPlatformStuff.getPlayerSleepCap(p);
        c.acceptFromServer(this.id, this.insomniaElapse, this.sleepTime, this.consecutiveNights, this.homeBedNights);
    }
}
