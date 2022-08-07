/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

<template>
  <div class="g-player-list">
    <div class="list-head" v-show="roomTitle">
      <div class="title">{{ roomTitle }}</div>
      <div class="ctrl">
        <GImageButton
          class="img-btn"
          v-if="isRoomOwner && isTeam"
          :status="roomMicStatus"
          @click="muteOtherPlayersByOwner"
          :img-on="imgBtnMicOn"
          :img-off="imgBtnMicOff"
          :img-disabled="imgBtnMicDisabled"
          :disabled="allPlayers.length <= 1"
        ></GImageButton>
        <GImageButton
          class="img-btn"
          v-show="roomTitle"
          :status="roomAudioStatus"
          @click="muteAll"
          :img-on="imgBtnSpeakerOn"
          :img-off="imgBtnSpeakerOff"
        ></GImageButton>
      </div>
    </div>
    <div class="list-body">
      <div
        class="player-item"
        v-for="(item, index) in allPlayers"
        :key="item.playerId || index"
        :class="{ 'is-self': isSelf(item.playerId) }"
      >
        <img class="member" :src="imgMember" />
        <div class="player-id">{{ item.playerId }}</div>
        <div class="append">
          <GImageButton
            class="img-btn"
            size="small"
            :status="getPlayerAudioStatus(item.playerId, item.isAudioMuted)"
            @click="muteAudio(item)"
            :img-on="imgBtnSpeakerOn"
            :img-off="imgBtnSpeakerOff"
            :img-gif="imgBtnSpeakerGif"
          ></GImageButton>
          <GImageButton
            class="img-btn"
            size="small"
            v-show="isTeam"
            :status="item.isPlayerMuted ? 'off' : 'on'"
            @click="mutePlayerByOwner(item)"
            :img-on="imgBtnMicOn"
            :img-off="imgBtnMicOff"
          ></GImageButton>
          <div class="label" v-show="isOwner(item.playerId)">房主</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, inject, onMounted, onUnmounted, reactive, ref, toRefs } from 'vue';
import { RoomType, Room, NationalRole } from '@/sdk/GMME';
import { commonReact, getGameMediaEngine, updateGameRoom, pushLog, LogType } from '@/api/engine';
import { GameRoom, Player } from '@/api/room';
import IMG_MIC_ON from '@/assets/btn_mic_on.png';
import IMG_MIC_OFF from '@/assets/btn_mic_off.png';
import IMG_MIC_DISABLED from '@/assets/btn_mic_disabled.png';
import IMG_SPEAKER_ON from '@/assets/btn_speaker_on.png';
import IMG_SPEAKER_OFF from '@/assets/btn_speaker_off.png';
import IMG_SPEAKER_GIF from '@/assets/btn_speaker_speaking.gif';
import IMG_MEMBER from '@/assets/member.png';
import GImageButton from '@/components/ImageButton.vue';

export default defineComponent({
  name: 'g-player-list',
  components: {
    GImageButton,
  },
  setup() {
    const data = reactive({
      visible: false,
      speakers: [] as string[],
      allRooms: commonReact.allRooms,
      isMicDisabled: false,
      isAllAudioMuted: false,
    });

    const curRoomId = inject('curRoomId', ref(''));
    const curPlayerId = inject('curPlayerId', ref(''));

    const isRoomOwner = computed(() => isOwner(curPlayerId.value));
    const allPlayers = computed(() => {
      const room = data.allRooms.get(curRoomId.value);
      if (!room) {
        return [];
      }
      return room.players.reduce((pre: Player[], cur: Player) => {
        if (curPlayerId.value === cur.playerId) {
          return [cur, ...pre];
        }
        return [...pre, cur];
      }, []);
    });
    const roomMicStatus = computed(() => (data.allRooms.get(curRoomId.value)?.isOtherPlayersMuted ? 'off' : 'on'));
    const roomAudioStatus = computed(() => (data.allRooms.get(curRoomId.value)?.isAudioMuted ? 'off' : 'on'));

    const isOwner = (playerId: string) => data.allRooms.get(curRoomId.value)?.ownerId === playerId;
    const isSelf = (playerId: string) => curPlayerId.value === playerId;

    const isTeam = computed(() => data.allRooms.get(curRoomId.value)?.roomType === RoomType.TEAM);
    const isNational = computed(() => data.allRooms.get(curRoomId.value)?.roomType === RoomType.NATIONAL);
    const roomTitle = computed(() => {
      let title = '';
      const playerLength = data.allRooms.get(curRoomId.value)?.players.length;
      if (isTeam.value) title = `小队成员 (${playerLength})`;
      else if (isNational.value) title = `国战成员 (${playerLength})`;
      return title;
    });

    const isCommander = (player: Player) => {
      return player.playerRole === NationalRole.COMMANDER;
    };

    const muteAll = () => {
      const room = data.allRooms.get(curRoomId.value) as GameRoom;
      const mutedSuccess = getGameMediaEngine().muteAllPlayers(curRoomId.value, !room.isAudioMuted);
      pushLog(`Mute all players, muted status: ${!room.isAudioMuted}, result: ${mutedSuccess}`);
      if (mutedSuccess) {
        room.isAudioMuted = !room.isAudioMuted;
        data.isAllAudioMuted = room.isAudioMuted;
        const { players } = room;
        for (const player of players) {
          if (curPlayerId.value !== player.playerId) {
            player.isAudioMuted = room.isAudioMuted;
          }
        }
      }
    };

    const muteAudio = (item: Player) => {
      if (curPlayerId.value === item.playerId) {
        return;
      }
      const mutedSuccess = getGameMediaEngine().mutePlayer(curRoomId.value, item.playerId, !item.isAudioMuted);
      pushLog(`Mute player: ${item.playerId || null}, muted status: ${!item.isAudioMuted}, result: ${mutedSuccess}`);
      if (mutedSuccess) {
        item.isAudioMuted = !item.isAudioMuted;
      }
    };

    const muteOtherPlayersByOwner = async () => {
      try {
        pushLog('Start forbid all players');
        const room = data.allRooms.get(curRoomId.value) as GameRoom;
        await getGameMediaEngine().forbidAllPlayers(curRoomId.value, !room.isOtherPlayersMuted);
        pushLog('Succeeded in sending the command for forbid all players');
        data.isMicDisabled = room.isOtherPlayersMuted;
        const roomInfo = (await getGameMediaEngine().getRoom(curRoomId.value)) as Room;
        updateGameRoom(curRoomId.value, roomInfo);
        const { players } = room;
        for (const player of players) {
          if (curPlayerId.value !== player.playerId) {
            player.isPlayerMuted = !room.isOtherPlayersMuted;
          }
        }
      } catch (error) {
        pushLog(`Forbid all players error: ${error?.message}`, LogType.ERROR);
      }
    };

    const mutePlayerByOwner = async (item: Player) => {
      if (!isOwner(curPlayerId.value)) {
        pushLog('Mute player by owner error: you are not room owner', LogType.ERROR);
        return;
      }
      try {
        await getGameMediaEngine().forbidPlayer(curRoomId.value, item.playerId, !item.isPlayerMuted);
        item.isPlayerMuted = !item.isPlayerMuted;
        pushLog(`Mute player by owner succeeded, player: ${item.playerId}, mute status: ${!item.isPlayerMuted}`);
      } catch (error) {
        pushLog(
          `Mute player by owner error, player: ${item.playerId}, mute status: ${!item.isPlayerMuted}, error: ${
            error?.message
          }`,
          LogType.ERROR,
        );
      }
    };

    const getPlayerAudioStatus = (playerId: string, isAudioMuted: boolean) => {
      if (isAudioMuted) {
        return 'off';
      } else if (data.speakers.includes(playerId)) {
        return 'gif';
      } else {
        return 'on';
      }
    };

    let clearSpeakerTimout: number;
    const updateSpeakers = (roomId: string, openIds: string[]) => {
      if (!openIds || !openIds.length) {
        clearSpeakerTimout = setTimeout(() => {
          data.speakers = [];
        }, 1000);
        return;
      }
      clearTimeout(clearSpeakerTimout);
      if (roomId === curRoomId.value) {
        data.speakers = [...openIds];
        clearSpeakerTimout = setTimeout(() => {
          data.speakers = [];
        }, 3000);
      }
    };

    const updatePlayerMute = (roomId: string, openIds: string[], isForbidden: boolean) => {
      pushLog(
        `Command 'forbiddenByOwner': roomId: ${roomId}, openIds: [${openIds.join()}], isForbidden: ${isForbidden}`,
      );
      const targetRoom = data.allRooms.get(roomId);
      if (!targetRoom) {
        return;
      }
      for (const player of targetRoom.players) {
        if (openIds.includes(player.playerId)) {
          player.isPlayerMuted = isForbidden;
        }
      }
    };

    onMounted(() => {
      getGameMediaEngine()?.on('speakersDetection', updateSpeakers);
      getGameMediaEngine()?.on('forbiddenByOwner', updatePlayerMute);
    });
    onUnmounted(() => {
      getGameMediaEngine()?.off('speakersDetection', updateSpeakers);
      getGameMediaEngine()?.off('forbiddenByOwner', updatePlayerMute);
    });

    return {
      ...toRefs(data),
      imgBtnMicOn: IMG_MIC_ON,
      imgBtnMicOff: IMG_MIC_OFF,
      imgBtnMicDisabled: IMG_MIC_DISABLED,
      imgBtnSpeakerOn: IMG_SPEAKER_ON,
      imgBtnSpeakerOff: IMG_SPEAKER_OFF,
      imgBtnSpeakerGif: IMG_SPEAKER_GIF,
      imgMember: IMG_MEMBER,
      curPlayerId,
      curRoomId,
      allPlayers,
      isRoomOwner,
      isTeam,
      roomTitle,
      roomMicStatus,
      roomAudioStatus,
      isSelf,
      isOwner,
      isCommander,
      muteAll,
      muteAudio,
      muteOtherPlayersByOwner,
      mutePlayerByOwner,
      getPlayerAudioStatus,
    };
  },
});
</script>
<style lang="scss" scoped>
.g-player-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: rgba($color: #2d2852, $alpha: 0.7);
  border-radius: 4px;
  overflow: hidden;
  .img-btn {
    margin-left: 10px;
  }
  .list-head {
    display: flex;
    align-items: center;
    background-color: #3a2d7e;
    padding: 0px 20px;
    height: 44px;
    line-height: 50px;
    .ctrl {
      width: 100px;
      text-align: right;
    }
    .title {
      flex: 1;
      color: white;
      font-size: 16px;
    }
  }
  .list-body {
    color: #a497df;
    height: calc(100% - 50px);
    overflow-y: scroll;
    &::-webkit-scrollbar {
      width: 0px;
      background-color: #1c1d32;
    }
    &::-webkit-scrollbar-thumb {
      background-color: #2a284f;
    }
    &:hover {
      &::-webkit-scrollbar-thumb {
        background-color: #483c84;
        cursor: pointer;
      }
    }
    .player-item {
      display: flex;
      align-items: center;
      padding: 10px 0px;
      margin: 0 30px;
      border-bottom: 1px solid darken($color: #534b7c, $amount: 10);
      &.is-self {
        color: #66befc;
      }
      .member {
        height: 30px;
        margin-right: 10px;
        margin-left: -10px;
      }
      .player-id {
        flex: 1;
      }
      .append {
        width: 120px;
        padding-right: 4px;
        text-align: right;
        display: flex;
        flex-direction: row-reverse;
        align-items: center;
        margin-right: -14px;
        .label {
          flex: 1;
        }
      }
    }
  }
}
</style>
