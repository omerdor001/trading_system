import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }

  connect(user, onMessageReceived) {
    const socket = new SockJS('http://localhost:8082/ws');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({}, (frame) => {
      console.log('Connected: ' + frame);
      this.connected = true;
      this.subscribe(user, onMessageReceived);
    }, this.onError);
  }

  subscribe(user, onMessageReceived) {
    if (this.connected) {
      this.stompClient.subscribe(`/user/${user}/notifications`, (message) => {
        onMessageReceived(message.body);
      });
    }
  }

  unsubscribe(user) {
    if(this.connected) {
      this.stompClient.unsubscribe(`/user/${user}/notifications`)
    }
  }

  onError(error) {
    console.log('WebSocket connection error: ' + error);
  }

  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    this.connected = false;
    console.log('Disconnected');
  }
}

export default new WebSocketService();
