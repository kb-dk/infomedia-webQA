const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    publicPath: process.env.BASE_URL,
    // publicPath: '/infomedia-webqa/',
    // publicPath: './',
    assetsDir: 'static',
    indexPath: 'index.html',
    chainWebpack: config => {
        config
            .plugin('html')
            .tap(args => {
                args[0].template = './public/index.html'
                args[0].title = 'infomedia-webqa'
                return args
            })
    }
})

