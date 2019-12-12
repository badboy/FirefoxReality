const CUSTOM_USER_AGENT = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.21 (KHTML, like Gecko) Version/9.2 Safari/602.1.21';
const LOGTAG = '[firefoxreality:webcompat:youtube]';
const YT_SELECTORS = {
  disclaimer: '.yt-alert-message, yt-alert-message',
  moviePlayer: '#movie_player'
};
const YT_PATHS = {
  watch: '/watch'
};
const LOGTAG = '[firefoxreality:webcompat:youtube]';
const ENABE_LOGS = true;
const logDebug = (...args) => ENABE_LOGS && console.log(LOGTAG, ...args);
const logError = (...args) => ENABE_LOGS && console.error(LOGTAG, ...args);

class YoutubeExtension {
    constructor(player) {
        this.player = null;
    }

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

    overrideQuality() {
        const preferredLevels = ['hd2880', 'hd2160','hd1440', 'hd1080', 'hd720', 'large', 'medium'];
        const currentLevel = player.getPlaybackQuality();
        logDebug(`Video getPlaybackQuality: ${currentLevel}`);

        let availableLevels = player.getAvailableQualityLevels();
        logDebug(`Video getAvailableQualityLevels: ${availableLevels}`);
        for (level of preferredLevels) {
            if (availableLevels.indexOf(level) >= 0) {
                if (currentLevel !== level) {
                    player.setPlaybackQualityRange(level, level);
                    logDebug(`Video setPlaybackQualityRange: ${level}`);
                    logDebug(`Video getPlaybackQuality: ${currentLevel}`);
                }
                return true;
            }
        }
       return false;
    }

    overrideQualityRetry() {
        this.retry("overrideQuality", () => this.overrideQuality());
    }

    detectImmersiveVideo() {

    }

    onFullScreenChange() {

    }

    // Utility function to retry tasks max n times until the execution is successful.
    retry(taskName, task, attempts = 10, interval = 200) {
        if (attempts > 0) {
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
            setTimeout(() => {
                this.retry(taskName, task, attempts, interval);
            })
        }
    }
}

const youtube = new YoutubeExtension();
youtube.overrideUA();
youtube.overrideViewport();

window.addEventListener('load', () => {
    // Wait until video has loaded the first frame to force quality change.
    // This prevents the infinite spinner problem.
    // See https://github.com/MozillaReality/FirefoxReality/issues/1433
    var video = document.getElementsByTagName("video")[0];
    if (video.readyState >= 2) {
      youtube.overrideQualityRetry();
    } else {
       video.addEventListener("loadeddata", () => youtube.overrideQualityRetry());
    }
});

window.addEventListener('fullscreenchange', () => {
    youtube.onFullScreenChange(document.fullscreenElement);
});

window.addEventListener('pushstate', () => youtube.detectImmersiveVideo());
window.addEventListener('popstate', () => youtube.detectImmersiveVideo());
