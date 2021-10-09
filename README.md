# Project 2 - *Tweeter*

**Tweeter** is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **3.5** hours for part 1 and **4** hours for part 2

## User Stories

The following **required** functionality is completed:

- [x] User can **compose and post a new tweet**
  - [x] User can click a “Compose” icon in the Action Bar on the top right
  - [x] User can then enter a new tweet and post this to twitter
  - [x] User is taken back to home timeline with **new tweet visible** in timeline
  - [x] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
  - [x] User can **see a counter with total number of characters left for tweet** on compose tweet page

The following **optional** features are implemented:

- [x] User is using **"Twitter branded" colors and styles**
- [x] User can click links in tweets to launch the web browser 
- [ ] User can **select "reply" from detail view to respond to a tweet**
- [x] The "Compose" action is moved to a FloatingActionButton instead of on the AppBar
- [ ] Compose tweet functionality is build using modal overlay
- [x] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.org/android/Using-Parceler).
- [ ] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
- [ ] When a user leaves the compose view without publishing and there is existing text, prompt to save or delete the draft. If saved, the draft should then be **persisted to disk** and can later be resumed from the compose view.
- [ ] Enable your app to receive implicit intents from other apps. When a link is shared from a web browser, it should pre-fill the text and title of the web page when composing a tweet. 

The following **additional** features are implemented:

- [x] Verified users have a checkmark next to their name.
- [x] Parsing of API responses (e.g. converting JSON to a list of `Tweet` objects) is handled by the `TweetClient` instead of the `TimelineActivity`.
- [x] For development purposes, the `TweetClient` can be configured to load sample data from a JSON file instead of using the real Twitter API.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://raw.githubusercontent.com/john-royal/Tweeter/main/TweeterWalkthrough-3.gif' title='Video Walkthrough' alt='Video Walkthrough' width='200px' />

## Notes

- **Twitter API:** The timeline would fail to load and I couldn’t figure out why. Eventually, I found that I hit the rate limit, and obtained my own API keys to resolve the issue.

## Open-source libraries used

- [Android Async HTTP](https://github.com/codepath/CPAsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2021 John Royal

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
