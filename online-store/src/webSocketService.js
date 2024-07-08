class WebSocketService {
    constructor() {
      this.socket = null;
      this.callbacks = [];
    }
  
    connect(url) {
      this.socket = new WebSocket(url);
      
      this.socket.onopen = () => {
        console.log('WebSocket connection opened');
      };
  
      this.socket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        this.callbacks.forEach(callback => callback(message));
      };
  
      this.socket.onclose = () => {
        console.log('WebSocket connection closed');
        this.socket = null;
      };
  
      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };
    }
  
    disconnect() {
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
    }
  
    subscribe(callback) {
      this.callbacks.push(callback);
    }
  
    unsubscribe(callback) {
      this.callbacks = this.callbacks.filter(cb => cb !== callback);
    }
  }
  
  const webSocketService = new WebSocketService();
  export default webSocketService;