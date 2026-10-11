package project.sonora.poi.apache;

public class Musica {
   private Integer idMusica;
   private String nomeArtista;
   private String urlSpotify;
   private String trackMusica;
   private String album;
   private String albumTipo;
   private Double danceability;
   private Double energy;
   private Double loudness;
   private Double speechiness;
   private Double acousticness;
   private Double valence;
   private Double tempo;
   private Double durationMs;
   private String titleMusica;
   private Integer views;
   private Integer likes;
   private Integer comentarios;
   private Integer streams;

 public Musica(Integer idMusica, String nomeArtista, String urlSpotify, String trackMusica, String album, String albumTipo, Double danceability, Double energy, Double loudness, Double speechiness, Double acousticness, Double valence, Double tempo, Double durationMs, String titleMusica, Integer views, Integer likes, Integer comentarios, Integer streams) {
  this.idMusica = idMusica;
  this.nomeArtista = nomeArtista;
  this.urlSpotify = urlSpotify;
  this.trackMusica = trackMusica;
  this.album = album;
  this.albumTipo = albumTipo;
  this.danceability = danceability;
  this.energy = energy;
  this.loudness = loudness;
  this.speechiness = speechiness;
  this.acousticness = acousticness;
  this.valence = valence;
  this.tempo = tempo;
  this.durationMs = durationMs;
  this.titleMusica = titleMusica;
  this.views = views;
  this.likes = likes;
  this.comentarios = comentarios;
  this.streams = streams;
 }

 public Integer getIdMusica() {
  return idMusica;
 }

 public void setIdMusica(Integer idMusica) {
  this.idMusica = idMusica;
 }

 public String getNomeArtista() {
  return nomeArtista;
 }

 public void setNomeArtista(String nomeArtista) {
  this.nomeArtista = nomeArtista;
 }

 public String getUrlSpotify() {
  return urlSpotify;
 }

 public void setUrlSpotify(String urlSpotify) {
  this.urlSpotify = urlSpotify;
 }

 public String getTrackMusica() {
  return trackMusica;
 }

 public void setTrackMusica(String trackMusica) {
  this.trackMusica = trackMusica;
 }

 public String getAlbum() {
  return album;
 }

 public void setAlbum(String album) {
  this.album = album;
 }

 public String getAlbumTipo() {
  return albumTipo;
 }

 public void setAlbumTipo(String albumTipo) {
  this.albumTipo = albumTipo;
 }

 public Double getDanceability() {
  return danceability;
 }

 public void setDanceability(Double danceability) {
  this.danceability = danceability;
 }

 public Double getEnergy() {
  return energy;
 }

 public void setEnergy(Double energy) {
  this.energy = energy;
 }

 public Double getLoudness() {
  return loudness;
 }

 public void setLoudness(Double loudness) {
  this.loudness = loudness;
 }

 public Double getSpeechiness() {
  return speechiness;
 }

 public void setSpeechiness(Double speechiness) {
  this.speechiness = speechiness;
 }

 public Double getAcousticness() {
  return acousticness;
 }

 public void setAcousticness(Double acousticness) {
  this.acousticness = acousticness;
 }

 public Double getValence() {
  return valence;
 }

 public void setValence(Double valence) {
  this.valence = valence;
 }

 public Double getTempo() {
  return tempo;
 }

 public void setTempo(Double tempo) {
  this.tempo = tempo;
 }

 public Double getDurationMs() {
  return durationMs;
 }

 public void setDurationMs(Double durationMs) {
  this.durationMs = durationMs;
 }

 public String getTitleMusica() {
  return titleMusica;
 }

 public void setTitleMusica(String titleMusica) {
  this.titleMusica = titleMusica;
 }

 public Integer getViews() {
  return views;
 }

 public void setViews(Integer views) {
  this.views = views;
 }

 public Integer getLikes() {
  return likes;
 }

 public void setLikes(Integer likes) {
  this.likes = likes;
 }

 public Integer getComentarios() {
  return comentarios;
 }

 public void setComentarios(Integer comentarios) {
  this.comentarios = comentarios;
 }

 public Integer getStreams() {
  return streams;
 }

 public void setStreams(Integer streams) {
  this.streams = streams;
 }

 @Override
 public String toString() {
  return "Musica{" +
          "idMusica=" + idMusica +
          ", nomeArtista='" + nomeArtista + '\'' +
          ", urlSpotify='" + urlSpotify + '\'' +
          ", trackMusica='" + trackMusica + '\'' +
          ", album='" + album + '\'' +
          ", albumTipo='" + albumTipo + '\'' +
          ", danceability=" + danceability +
          ", energy=" + energy +
          ", loudness=" + loudness +
          ", speechiness=" + speechiness +
          ", acousticness=" + acousticness +
          ", valence=" + valence +
          ", tempo=" + tempo +
          ", durationMs=" + durationMs +
          ", titleMusica='" + titleMusica + '\'' +
          ", views=" + views +
          ", likes=" + likes +
          ", comentarios=" + comentarios +
          ", streams=" + streams +
          '}';
 }
}
