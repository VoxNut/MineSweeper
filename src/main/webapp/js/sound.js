const ctx = document.body.dataset.contextPath || "";

const SOUNDS = {
    click:   new Audio(`${ctx}/sound/Click.mp3`),
    flag:    new Audio(`${ctx}/sound/Flag.mp3`),
    boom:    new Audio(`${ctx}/sound/Boom.mp3`),
    lose:    new Audio(`${ctx}/sound/Lose.mp3`),
    victory: new Audio(`${ctx}/sound/Victory.mp3`)
};

// Preload all sounds
Object.values(SOUNDS).forEach((audio) => {
    audio.preload = "auto";
    audio.volume = 0.5;
});

export function playSound(name) {
    const audio = SOUNDS[name];
    if (!audio) {
        return;
    }
    // Reset to start so rapid triggers don't overlap silently
    audio.currentTime = 0;
    audio.play().catch(() => {
        // Browsers may block autoplay before user interaction — ignore
    });
}

export function setVolume(level) {
    const clamped = Math.max(0, Math.min(1, level));
    Object.values(SOUNDS).forEach((audio) => {
        audio.volume = clamped;
    });
}
