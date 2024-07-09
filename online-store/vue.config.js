const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080, // Specify a port number that you want to use
    open: true, // Open the default browser automatically on startup
  },
});