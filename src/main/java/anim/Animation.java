package anim;

import java.util.List;

public class Animation {

    private int currentFrame;

    private final List<AnimatedFrame> frames;

    private final String name;
    
    private final double duration;

    public Animation(String name, List<AnimatedFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    }

    public AnimatedFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    public double getDuration() {
        return this.duration;        
    }
    
    public List<AnimatedFrame> getFrames() {
        return frames;
    }

    public String getName() {
        return name;
    }

    public AnimatedFrame getNextFrame(double frameTime) {
        nextFrame(frameTime);
        return this.frames.get(currentFrame);
    }

    //used as a flag to control speed of animation; if it wouldn't be there, it would go very fast
    double currentAniTime;

    public void nextFrame(double frameTime) {
        if (currentAniTime < 0){
            currentAniTime += frameTime;
            return;
        }
        currentAniTime = 0;
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            currentFrame = 0;
        } else {
            currentFrame = nextFrame;
        }
    }

}
