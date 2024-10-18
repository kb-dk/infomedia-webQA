const { defineConfig } = require('@vue/cli-service')
const path = require('path');
module.exports = defineConfig({
  transpileDependencies: true,
  devServer:{
    proxy:{
      '/kuana-ndb-api':{
        target:"http://canopus.statsbiblioteket.dk:8641/kuana-newspapers-db",
        changeOrigin: true,
        pathRewrite: { '^/kuana-ndb-api': '' },
        secure: false,
      }
    }
  },
  publicPath:"/newspaper-qa/",
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
      },
    },
  },
})
