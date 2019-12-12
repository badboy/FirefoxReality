'use strict';
const CUSTOM_USER_AGENT = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.21 (KHTML, like Gecko) Version/9.2 Safari/602.1.21';
const LOGTAG = '[firefoxreality:webcompat:youtube]';
const VIDEO_PROJECTION_PARAM = 'mozVideoProjection';
const YT_SELECTORS = {
  disclaimer: '.yt-alert-message, yt-alert-message',
  moviePlayer: '#movie_player'
};
const ENABLE_LOGS = true;
const logDebug = (...args) => ENABLE_LOGS && console.log(LOGTAG, ...args);
const logError = (...args) => ENABLE_LOGS && console.error(LOGTAG, ...args);

class YoutubeExtension {
    // We set a custom UA to force Youtube to display the most optimal
    // and high-resolution layout available for playback in a mobile VR browser.
    overrideUA() {
        Object.defineProperty(navigator, 'userAgent', {
            get: () => CUSTOM_USER_AGENT
        });
        logDebug(`Youtube UA overriden to: ${navigator.userAgent}`)
    }

    // If missing, inject a `<meta name="viewport">` tag to trigger YouTube's mobile layout.
    overrideViewport() {
        let viewport = document.querySelector('meta[name="viewport"]');
        if (!viewport) {
            document.documentElement.insertAdjacentHTML('afterbegin',
            `<meta name="viewport" content="width=device-width, initial-scale=1">`);
            logDebug(`Youtube viewport added`);
        } else {
            logDebug(`Youtube already had a viewport: ${viewport}`);
        }
    }

    // Select a better youtube video quality
    overrideQuality() {
        logDebug('overrideQuality attempt');
        const player = this.getPlayer();
        if (!player) {
            logDebug('player not ready');
            return false;
        }
        const preferredLevels = this.getPreferredQualities();
        const currentLevel = player.getPlaybackQuality();
        logDebug(`Video getPlaybackQuality: ${currentLevel}`);

        let availableLevels = player.getAvailableQualityLevels();
        logDebug(`Video getAvailableQualityLevels: ${availableLevels}`);
        for (const level of preferredLevels) {
            if (availableLevels.indexOf(level) >= 0) {
                if (currentLevel !== level) {
                    player.setPlaybackQualityRange(level, level);
                    logDebug(`Video setPlaybackQualityRange: ${level}`);
                } else {
                    logDebug('Best quality already selected');
                }
                return true;
            }
        }
       return false;
    }

    overrideQualityRetry() {
        this.retry("overrideQuality", () => this.overrideQuality());
    }

    // Automatically select a video projection if needed
    overrideVideoProjection() {
        if (!this.isWatchingPage()) {
            logDebug("is not watching page");
            return; // Only override projection in the Youtube watching page.
        }
        const qs = new URLSearchParams(window.location.search);
        if (qs.get(VIDEO_PROJECTION_PARAM)) {
            logDebug(`Video has already a video projection selected: ${qs.get(VIDEO_PROJECTION_PARAM)}`);
            return;
        }
        // There is no standard API to detect video projection yet.
        // Try to infer it from the video disclaimer for now.
        const disclaimer = document.querySelector(YT_SELECTORS.disclaimer);
        const is360 = disclaimer && disclaimer.textContent.includes('360');
        if (is360) {
            qs.set('mozVideoProjection', '360_auto');
            logDebug(`Video projection set to: ${qs.get(VIDEO_PROJECTION_PARAM)}`);
            this.updateURL(qs);
        } else {
            logDebug(`Video is flat, no projection selected`);
        }
    }

    overrideClick(event) {
        if (!this.isWatchingPage() || !this.hasVideoProjection() || document.fullscreenElement) {
            return; // Only override click in the Youtube watching page for 360 videos.
        }

        if (event.target.closest(YT_SELECTORS.moviePlayer) && !event.target.closest('.ytp-chrome-bottom')) {
            const player = this.getPlayer();
            player && player.requestFullscreen();
        }
    }

    // Runs the callback when the video is ready (has loaded the first frame).
    waitForVideoReady(callback) {
        this.retry("VideoReady", () => {
            var video = document.getElementsByTagName("video")[0];
            if (!video) {
                return false;
            }
            if (video.readyState >= 2) {
              callback();
            } else {
              video.addEventListener("loadeddata", callback, {once: true});
            }
            return true;
        });
    }

     // Get's the Youtube player elements which contains the API functions.
    getPlayer() {
        let player =  document.getElementById('movie_player');
        if (!player || !player.wrappedJSObject) {
            return null;
        }
        return player.wrappedJSObject;
    }

    // Get's the preferred video qualities for the current device.
    getPreferredQualities() {
        let all = ['hd2880', 'hd2160','hd1440', 'hd1080', 'hd720', 'large', 'medium'];
        return all;
    }

    // Returns true if we are in a video watching page.
    isWatchingPage() {
        return window.location.pathname.startsWith('/watch');
    }

    // Returns true if we are in a video watching page.
    hasVideoProjection() {
        const qs = new URLSearchParams(window.location.search);
        return !!qs.get(VIDEO_PROJECTION_PARAM);
    }

    // Utility function to retry tasks max n times until the execution is successful.
    retry(taskName, task, attempts = 10, interval = 200) {
        let succeeded = false;
        try {
            succeeded = task();
        } catch (ex) {
            logError(`Got exception runnning ${taskName} task: ${ex}`);
        }
        if (succeeded) {
            logDebug(`${taskName} succeeded`);
            return;
        }
        attempts--;
        logDebug(`${taskName} failed. Remaining attempts ${attempts}`);
        if (attempts > 0) {
            setTimeout(() => {
                this.retry(taskName, task, attempts, interval);
            })
        };
    }
    // Utility function to replace current URL params and update history.
    updateURL(qs) {
        const newUrl = `${window.location.pathname}?${qs}`;
        window.history.replaceState({}, document.title, newUrl);
        logDebug(`update URL to ${newUrl}`);
    }
}

logDebug(`Initializing youtube extension in frame: ${window.location.href}`);
const youtube = new YoutubeExtension();
youtube.overrideUA();
youtube.overrideViewport();

window.addEventListener('load', () => {
    logDebug('page load');
    youtube.overrideVideoProjection();
    // Wait until video has loaded the first frame to force quality change.
    // This prevents the infinite spinner problem.
    // See https://github.com/MozillaReality/FirefoxReality/issues/1433
    if (youtube.isWatchingPage()) {
        youtube.waitForVideoReady(() => youtube.overrideQualityRetry());
    }
});

window.addEventListener('pushstate', () => youtube.overrideVideoProjection());
window.addEventListener('popstate', () => youtube.overrideVideoProjection());
window.addEventListener('click', evt => youtube.overrideClick());
