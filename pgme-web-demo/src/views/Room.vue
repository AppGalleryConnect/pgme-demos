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
  <div ref="page" id="page" class="page">
    <div class="page-body">
      <div class="input-line">
        <div class="input-name">房间切换：</div>
        <GSelect class="input-item" :value="curRoomId" :options="roomOptions" @value-change="changeRoom"></GSelect>
      </div>
      <div class="input-line">
        <div class="input-name">房间ID：</div>
        <GInput class="input-item" v-model:value="inputRoomId" placeholder="ID必填"></GInput>
      </div>
      <div class="btn-line">
        <GButton small type="primary" @click="joinTeamRoom" :disabled="!canBeClicked">创建/加入小队</GButton>
        <GButton small type="primary" @click="showJoinNationalDialog = true" :disabled="!canBeClicked"
          >创建/加入国战</GButton
        >
      </div>
      <div class="btn-line">
        <GCheckBox name="mic" v-model:value="micChecked" @status-change="changeMic">开麦</GCheckBox>
        <GButton small type="cancel" @click="leaveRoom" :disabled="!allRooms.size || !canBeClicked">离开房间</GButton>
      </div>
      <div class="player-panel" :style="playerPanelAutoHeight">
        <div class="panel-body">
          <GPlayerList class="panel-item" :class="{ 'align-left': true, 'ease-left': showLog }"></GPlayerList>
          <GRuntimeLog class="panel-item"></GRuntimeLog>
        </div>
      </div>
      <div class="btn-line action-btn">
        <GButton small type="primary" @click="showLog = !showLog">{{ showLog ? '房间成员' : '查看日志' }}</GButton>
        <GButton small type="cancel" @click="destroyEngine">销毁引擎</GButton>
      </div>
      <GNationalRoleDialog v-model:visible="showJoinNationalDialog" @ok="onRoleSelected"></GNationalRoleDialog>
      <GTransferOwnerDialog
        v-model:visible="showTransferOwnerDialog"
        :player-list="otherPlayers"
        @ok="onOwnerSelected"
      ></GTransferOwnerDialog>
    </div>
    <div id="demo"></div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, onMounted, onUnmounted, provide, reactive, ref, toRefs } from 'vue';
import { commonReact, destroy, getGameMediaEngine, updateGameRoom, pushLog, LogType, clearLog } from '@/api/engine';
import { GameRoom, Player } from '@/api/room';
import { NationalRole, Room, RoomType, RoomStatus } from '@/sdk/GMME';
import { useRoute, useRouter } from 'vue-router';
import GButton from '@/components/Button.vue';
import GInput from '@/components/Input.vue';
import GSelect from '@/components/Select.vue';
import GNationalRoleDialog from '@/views/NationalRoleDialog.vue';
import GTransferOwnerDialog from '@/views/TransferOwnerDialog.vue';
import GCheckBox from '@/components/CheckBox.vue';
import GPlayerList from '@/views/PlayerList.vue';
import GRuntimeLog from '@/views/RuntimeLog.vue';

export default defineComponent({
  components: {
    GButton,
    GInput,
    GSelect,
    GNationalRoleDialog,
    GTransferOwnerDialog,
    GCheckBox,
    GPlayerList,
    GRuntimeLog,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const curRoomId = ref('');
    const curPlayerId = ref(route.query.openId);
    const data = reactive({
      visible: false,
      inputRoomId: '',
      micChecked: true,
      canBeClicked: true,
      curNationalRole: 0,
      allRooms: commonReact.allRooms,
      showJoinNationalDialog: false,
      showTransferOwnerDialog: false,
      playerPanelAutoHeight: { height: '300px' },
      showLog: false,
    });
    const isRoomOwner = computed(() => data.allRooms.get(curRoomId.value)?.ownerId === curPlayerId.value);
    const roomOptions = computed(() =>
      Array.from(data.allRooms.values()).map((room: GameRoom) => ({
        value: room.roomId,
        label: room.roomId,
      })),
    );

    const otherPlayers = computed(() => {
      const room = data.allRooms.get(curRoomId.value);
      if (!room) {
        return [];
      }
      return room.players
        .map((player: Player) => player.playerId)
        .filter((playerId: string) => curPlayerId.value !== playerId);
    });

    const isNationalMass = computed(
      () =>
        data.allRooms.get(curRoomId.value)?.roomType === RoomType.NATIONAL &&
        data.curNationalRole === NationalRole.MASSES,
    );
    provide('curRoomId', curRoomId);
    provide('curPlayerId', curPlayerId);

    const onWindowResize = function () {
      const h = document.getElementById('page')?.offsetHeight;
      let autoHeight = (h as number) - 300;
      autoHeight = autoHeight < 100 ? 100 : autoHeight;
      data.playerPanelAutoHeight = { height: autoHeight + 'px' };
    };

    let refreshRoom: number;
    onMounted(() => {
      refreshRoom = setInterval(() => {
        for (const roomId of data.allRooms.keys()) {
          const roomInfo = getGameMediaEngine().getRoom(roomId);
          updateGameRoom(roomId, roomInfo);
        }
      }, 1000);
      onWindowResize();
      window.addEventListener('resize', onWindowResize);
    });
    onUnmounted(() => {
      clearInterval(refreshRoom);
      window.removeEventListener('resize', onWindowResize);
    });

    const onRoleSelected = (role: NationalRole) => {
      joinNationalRoom(role);
    };

    const onOwnerSelected = (playerId: string) => {
      handleLeaveOk(playerId);
    };

    const handleLeaveOk = async (nextOwner: string) => {
      pushLog(`Start leaving the room: ${curRoomId.value}, next owner: ${nextOwner || null}`);
      pushLog('disable speakers detection');
      getGameMediaEngine().enableSpeakersDetection(curRoomId.value, 0);
      try {
        await getGameMediaEngine().leaveRoom(curRoomId.value, nextOwner);
        pushLog('Leaving the room succeeded');
      } catch (error) {
        pushLog(`Error leaving room: ${error?.message}`, LogType.ERROR);
        return;
      }
      data.allRooms.delete(curRoomId.value);
      if (data.allRooms.size === 0) {
        curRoomId.value = '';
        data.curNationalRole = 0;
        return;
      }
      const [roomId] = data.allRooms.entries().next().value;
      await changeRoom(roomId);
    };

    const leaveRoom = () => {
      const room = data.allRooms.get(curRoomId.value);
      if (room && room.players.length > 1 && isRoomOwner.value) {
        data.showTransferOwnerDialog = true;
      } else {
        handleLeaveOk('');
      }
    };

    const handleJoinResult = (roomId: string, roomType: RoomType) => {
      curRoomId.value = roomId;
      data.inputRoomId = '';
      const { players, ownerId, status } = getGameMediaEngine().getRoom(roomId) as Room;
      data.allRooms.set(roomId, {
        roomId,
        status,
        isAudioMuted: false,
        isOtherPlayersMuted: status === RoomStatus.MUTED,
        ownerId,
        players: players.map((player) => {
          return {
            playerId: player.openId,
            isAudioMuted: false,
            isPlayerMuted: player.isForbidden,
            playerRole: player.playerRole || null,
          };
        }),
        roomType: roomType,
      });
      getGameMediaEngine().enableSpeakersDetection(curRoomId.value, 100);
    };

    const joinTeamRoom = async () => {
      data.canBeClicked = false;
      try {
        pushLog('Start joining the team room');
        const roomId = await getGameMediaEngine().joinTeamRoom(data.inputRoomId);
        handleJoinResult(roomId, RoomType.TEAM);
        pushLog(`Joining the team room succeeded, roomId: ${roomId}`);
      } catch (error) {
        pushLog(`Failed to join the team room: ${error?.message}`, LogType.ERROR);
      }
      data.canBeClicked = true;
    };
    const joinNationalRoom = async (roleType: NationalRole) => {
      try {
        pushLog(`Start joining the national room, role: ${roleType}`);
        data.canBeClicked = false;
        const roomId = await getGameMediaEngine().joinNationalRoom(data.inputRoomId, roleType);
        data.curNationalRole = roleType;
        handleJoinResult(roomId, RoomType.NATIONAL);
        pushLog(`Joined the national room successfully, roomId: ${roomId}`);
      } catch (error) {
        pushLog(`Failed to join the national room: ${error?.message}`, LogType.ERROR);
      }
      data.canBeClicked = true;
    };
    const changeMic = (checked: boolean) => {
      try {
        getGameMediaEngine().enableMic(checked);
        pushLog('Switching the microphone optical status succeeded');
      } catch (error) {
        pushLog(`Failed to switch the microphone optical status: ${error?.message}`, LogType.ERROR);
      }
    };
    const changeRoom = async (toRoomId: string) => {
      try {
        pushLog('Start switch room');
        await getGameMediaEngine().switchRoom(toRoomId);
        const roomInfo = getGameMediaEngine().getRoom(toRoomId);
        updateGameRoom(toRoomId, roomInfo);
        pushLog('Switch room done');
      } catch (error) {
        pushLog(`Failed to switch rooms: ${error?.message}`, LogType.ERROR);
      } finally {
        getGameMediaEngine().enableSpeakersDetection(toRoomId, 100);
        curRoomId.value = toRoomId;
        pushLog(`Current roomId: ${curRoomId.value}`);
      }
    };

    const destroyEngine = async () => {
      try {
        await destroy();
        clearLog();
      } catch (error) {
        pushLog(`Failed to destroy the engine: ${error?.message}`, LogType.ERROR);
      }
      router.go(-1);
    };
    return {
      curRoomId,
      curPlayerId,
      ...toRefs(data),
      isRoomOwner,
      roomOptions,
      isNationalMass,
      handleLeaveOk,
      leaveRoom,
      joinTeamRoom,
      joinNationalRoom,
      changeMic,
      changeRoom,
      destroyEngine,
      onRoleSelected,
      onOwnerSelected,
      otherPlayers,
    };
  },
});
</script>
<style lang="scss" scoped>
.btn-line {
  padding: 0 16px;
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  &.action-btn {
    margin-top: 20px;
  }
}
.input-line {
  display: flex;
  margin-top: 14px;
  margin-bottom: 6px;
  padding: 0 16px;
  .input-name {
    width: 80px;
    display: inline-block;
    text-align: right;
    color: white;
    line-height: 30px;
  }
  .input-item {
    flex: 1;
  }
}
</style>
