import React from 'react';

export default class DailyNotices extends React.Component {
    render() {
        return <div>
            <iframe title="Daily Notices" sandbox src="https://www.scotscollege.school.nz/daily-notices/" className='w-full h-screen'>
                Please update to a browser that supports inline frames.
            </iframe>
        </div>
    }
}