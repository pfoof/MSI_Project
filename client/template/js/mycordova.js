const CACHE_NAME = 'mymsi-cache';
const PRE_CACHED_ASSETS = [
  '/images/add_small.png',
  '/images/add.png',  
  '/images/back.png',
  '/images/save.png',
  '/images/edit.png',
  '/images/delete.png',
  '/images/refresh.png',
  '/images/remove.png',      
  '/css/bootstrap.min.css',
  '/css/priori.css',
  '/js/bootstrap.min.js',
  '/js/html5shiv.min.js',
  '/js/respond.min.js',
  '/js/jquery-2.2.1.min.js',
  '/js/jquery.xdomainrequest.min.js',
  '/js/mycordova.js', 
  '/index.html'
];

self.addEventListener('install', function(event) {
  event.waitUntil(
    caches.open(CACHE_NAME).then(function(cache) {
      let cachePromises = PRE_CACHED_ASSETS.map(function(asset) {
        var url = new URL(asset, location.href);
        var request = new Request(url);
        return fetch(request).then(function(response) {
          return cache.put(asset, response);
        });
      });

      return Promise.all(cachePromises);
    }),
  );
});

self.addEventListener('activate', function(event) {
    event.waitUntil(
      caches.keys().then(function(cacheNames) {
        return Promise.all(
          // delete old caches
          cacheNames.map(function(cacheName) {
            if (cacheName !== CACHE_NAME) {
              return caches.delete(cacheName);
            }
          }),
        );
      }),
    );
  });

  self.addEventListener('fetch', function(event) {
    if (event.request.headers.get('accept').startsWith('text/html')) {
      event.respondWith(
        fetch(event.request).catch(error => {
          return caches.match('index.html');
        }),
      );
    }
  });