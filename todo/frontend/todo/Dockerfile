# Stage 1: Build dell'app Vue
FROM node:22 AS build
WORKDIR /app

# Copia i file di configurazione e installa le dipendenze
COPY package*.json ./
RUN npm install

# Copia tutto il codice sorgente (assicurati che il .dockerignore escluda file inutili)
COPY . .

# Esegui il build per la produzione (assicurati che il comando generi la cartella "dist")
RUN npm run build

# Stage 2: Immagine runtime con Nginx per servire i file statici
FROM nginx:stable-alpine
# Rimuove la configurazione default di Nginx
#RUN rm /etc/nginx/conf.d/default.conf

# (Opzionale) Se desideri usare una configurazione custom di Nginx,
# copia il file nginx.conf nel container:
# COPY nginx.conf /etc/nginx/conf.d/

# Copia i file generati dalla build nella cartella in cui Nginx serve i file statici
COPY --from=build /app/dist /usr/share/nginx/html

# Espone la porta 80 (la porta predefinita di Nginx)
EXPOSE 80

# Avvia Nginx in modalità "foreground"
CMD ["nginx", "-g", "daemon off;"]
