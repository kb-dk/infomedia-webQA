const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer:{
    proxy:{
      '/kuana-ndb-api':{
        target:"http://canopus.statsbiblioteket.dk:8641/kuana-newspapers-db/v1",
        changeOrigin: true,
        pathRewrite: { '^/kuana-ndb-api': '' },
        secure: false,
      }
    }
  },
  publicPath:"/kuana-ndb-wui/webQA/"
})
