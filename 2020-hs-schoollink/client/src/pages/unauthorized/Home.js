import ReactTypingEffect from 'react-typing-effect';
import React from 'react';

export default class Home extends React.Component {
    render() {
        return (
            <div class="relative overflow-hidden">
                <div class="max-w-screen-xl mx-auto">
                    <div class="relative z-10 pb-8 sm:pb-16 md:pb-20 lg:pb-28 xl:pb-50">
                        <div class="mt-10 mx-auto max-w-screen-xl px-4 sm:mt-12 sm:px-6 md:mt-16 lg:mt-20 lg:px-8 xl:mt-28">
                            <div class="sm:text-center table">
                                <h2 class="table-row block static text-4xl tracking-tight leading-10 font-extrabold text-gray-900 sm:text-5xl sm:leading-none md:text-6xl">
                                    <span>Your online </span>
                                    <br class="xl:hidden" />
                                    <span class="text-red-600"><ReactTypingEffect text={["prep diary.", "timetable manager.", "homework tracker."]} typingDelay={1000} eraseDelay={1000} speed={100} /></span>
                                    <br />
                                </h2>
                                <p class="table-row mt-3 text-base text-gray-600 sm:mt-5 sm:text-lg sm:max-w-xl sm:mx-auto md:mt-5 md:text-xl lg:mx-0">
                                    SchoolLink is an online application that helps Scots College students like you keep assignments in check, classes scheduled and grades up.
                                </p>
                                <div class="mt-8">
                                    <h5 class="text-lg leading-6 font-medium text-gray-900">Unfortunately, we're not quite ready for prime time just yet. Please check back later!</h5>
                                    <h5 class="text-lg leading-6 font-medium text-gray-400">Estimated launch date: July 2020 (Y12 SC)</h5>
                                </div>
                                <div class="mt-4">
                                    <span class="text-md font-light text-gray-500">(Authorised testers, please click 'Testers login' in the top bar)</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        )
    }
}