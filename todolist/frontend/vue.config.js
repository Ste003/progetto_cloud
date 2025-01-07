const { defineConfig } = require('@vue/cli-service')
module.exports = {
  devServer: {
      proxy: {
          '/api': {
              target: 'http://localhost:8081', // URL del backend
              changeOrigin: true,
              pathRewrite: {
                  '^/api': '', // Rimuove "/api" dal percorso della richiesta
              },
          },
      },
  },
};
