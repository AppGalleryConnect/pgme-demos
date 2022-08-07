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

import { RoomType, RoomStatus, NationalRole } from '@/sdk/GMME';

export type roomId = string;
export interface Player {
  playerId: string;
  isAudioMuted: boolean;
  isPlayerMuted: boolean;
  playerRole: NationalRole | null;
}
export interface GameRoom {
  players: Player[];
  ownerId: string;
  isAudioMuted: boolean;
  isOtherPlayersMuted: boolean;
  status: RoomStatus;
  roomType: RoomType;
  roomId: roomId;
}
export interface Event {
  key: string;
}
