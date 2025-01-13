const { defineConfig } = require('@vue/cli-service')
module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8081', // URL del backend
                changeOrigin: true, // Cambia l'origin della richiesta
                pathRewrite: {}, // NON rimuovere '/api'
            },
        },
    },
  };
  
