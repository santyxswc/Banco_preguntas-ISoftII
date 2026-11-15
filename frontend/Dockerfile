# Imagen del frontend web (RNF #3): servidor estático de Node sin dependencias de npm.
FROM node:20-alpine
WORKDIR /app
COPY server.mjs .
COPY public ./public
ENV PORT=5173
EXPOSE 5173
USER node
CMD ["node", "server.mjs"]
