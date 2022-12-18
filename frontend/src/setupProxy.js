const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        createProxyMiddleware('/api/ws', {
            target: 'wss://localhost:443',
            secure: false,
            ws: true,
            headers: { // for some reason changeOrigin does nothing with websockets
                "origin": "https://localhost:443"
            }
        })
    );
    app.use(
        '/api',
        createProxyMiddleware({
            target: 'https://localhost:443',
            secure: false,
            changeOrigin : true
        })
    );
    app.use(
        '/auth',
        createProxyMiddleware({
            target: 'https://localhost:443',
            secure: false,
            changeOrigin : true
        })
    );
};
