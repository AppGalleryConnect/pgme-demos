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

import { GameMediaEngine, EngineCreateParams, Room, RoomStatus } from '@/sdk/GMME';
import { GameRoom, Player, roomId } from '@/api/room';
import { reactive } from 'vue';

export enum LogType {
  INFO,
  ERROR,
}
interface LogData {
  type: LogType;
  time: string;
  message: string;
}

const jsrsasign = require('jsrsasign');

let gameMediaEngine: GameMediaEngine;
export const getGameMediaEngine = () => gameMediaEngine;

export const commonReact = reactive({
  allRooms: new Map() as Map<roomId, GameRoom>,
});

export const logsReact = reactive({
  maxLength: 100,
  logs: [] as LogData[],
});

export const pushLog = (message: string | object, type = LogType.INFO) => {
  const logTime = new Date();
  logsReact.logs.push({
    type,
    message: typeof message === 'object' ? JSON.stringify(message) : message,
    time: `${logTime.toLocaleDateString()} ${logTime.toLocaleTimeString()}`,
  });
  if (logsReact.logs.length > logsReact.maxLength) {
    logsReact.logs.shift();
  }
};

export const clearLog = () => (logsReact.logs = []);

export const updateGameRoom = (roomId: string, roomInfo: Room) => {
  const { ownerId, roomType, status } = roomInfo;
  const players = roomInfo.players.map((player) => {
    const { openId } = player;
    const existPlayer = commonReact.allRooms.get(roomId)?.players.find((player: Player) => player.playerId === openId);
    if (existPlayer) {
      return existPlayer;
    }
    const newPlayer: Player = {
      playerId: openId,
      isAudioMuted: false,
      isPlayerMuted: player.isForbidden,
      playerRole: player.playerRole || null,
    };
    return newPlayer;
  });
  if (commonReact.allRooms.has(roomId)) {
    const { isAudioMuted } = commonReact.allRooms.get(roomId) as GameRoom;
    commonReact.allRooms.set(roomId, {
      roomId,
      isAudioMuted,
      isOtherPlayersMuted: status === RoomStatus.MUTED,
      status,
      ownerId,
      players,
      roomType,
    });
  } else {
    commonReact.allRooms.set(roomId, {
      roomId,
      isAudioMuted: false,
      isOtherPlayersMuted: status === RoomStatus.MUTED,
      status: roomInfo.status,
      ownerId,
      players,
      roomType,
    });
  }
};

const addPlayer = (roomId: string, openId: string) => {
  updateGameRoom(roomId, gameMediaEngine.getRoom(roomId));
};

const removePlayer = (roomId: string, openId: string) => {
  updateGameRoom(roomId, gameMediaEngine.getRoom(roomId));
};

const getSign = (appId: string, openId: string) => {
  const gameSecret = `-----BEGIN PRIVATE KEY-----
${EngineParamsConfig.gameSecret}
-----END PRIVATE KEY-----`;
  const timestamp = Date.now().toString();
  const nonce = '1234567890';
  const sig = new jsrsasign.KJUR.crypto.Signature({
    alg: 'SHA256withRSAandMGF1',
    prvkeypem: gameSecret,
  });
  const newStr = `appId=${appId}&nonce=${nonce}&openId=${openId}&timestamp=${timestamp}`;
  sig.updateString(newStr);
  return { sign: jsrsasign.hextob64(sig.sign()), nonce, timestamp };
};

export const initEngine = async (openId: string, initOptions?: { security: boolean }) => {
  try {
    const signParams =
      initOptions?.security && EngineParamsConfig.gameSecret ? getSign(EngineParamsConfig.appId, openId) : {};
    const options: EngineCreateParams = {
      openId,
      audioId: 'demo',
      appId: EngineParamsConfig.appId,
      clientId: EngineParamsConfig.clientId,
      clientSecret: EngineParamsConfig.clientSecret,
      ...signParams,
    };
    gameMediaEngine = await GameMediaEngine.create(options);
    gameMediaEngine.on('playerOnline', addPlayer);
    gameMediaEngine.on('playerOffline', removePlayer);
  } catch (e) {
    return Promise.reject(e);
  }
};

export const destroy = async () => {
  try {
    await gameMediaEngine?.destroy();
  } finally {
    gameMediaEngine?.off('playerOnline', addPlayer);
    gameMediaEngine?.off('playerOffline', removePlayer);
    commonReact.allRooms.clear();
  }
};

export class EngineParamsConfig {
  static appId: string;
  static clientId: string;
  static clientSecret: string;
  static gameSecret: string;

  static setAppId(appId: string) {
    this.appId = appId;
  }

  static setClientId(clientId: string) {
    this.clientId = clientId;
  }

  static setClientSecret(clientSecret: string) {
    this.clientSecret = clientSecret;
  }

  static setGameSecret(gameSecret: string) {
    this.gameSecret = gameSecret;
  }
}
