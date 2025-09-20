/*
 * Copyright 2022 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.dailyrewards.network.message;

import de.markusbordihn.dailyrewards.Constants;
import de.markusbordihn.dailyrewards.data.RewardScreenType;
import de.markusbordihn.dailyrewards.rewards.RewardsScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public record MessageOpenRewardScreen(RewardScreenType rewardScreenType) implements CustomPacketPayload {

    private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

    public static final Type<MessageOpenRewardScreen> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "open_reward_screen"));

    public static final StreamCodec<FriendlyByteBuf, MessageOpenRewardScreen> CODEC =
        StreamCodec.of(
            (buf, msg) -> buf.writeEnum(msg.rewardScreenType),                // encoder
            buf -> new MessageOpenRewardScreen(buf.readEnum(RewardScreenType.class)) // decoder
        );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        if (rewardScreenType == null) {
            log.warn("Unable to open reward screen for player {} due to missing reward screen type!", player);
            return;
        }

        log.debug("Opening reward screen for player {} with type {} ...", player, rewardScreenType);

        switch (rewardScreenType) {
            case COMPACT -> RewardsScreen.openRewardCompactMenuForPlayer(player);
            case DEFAULT_OVERVIEW -> RewardsScreen.openRewardOverviewMenuForPlayer(player);
            case SPECIAL_OVERVIEW -> RewardsScreen.openRewardSpecialOverviewMenuForPlayer(player);
            default -> log.warn("Unknown reward screen type {} for player {}!", rewardScreenType, player);
        }
    }
}
