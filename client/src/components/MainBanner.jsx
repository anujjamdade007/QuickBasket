import React from 'react'
import { assets } from '../assets/assets'
import { Link } from 'react-router-dom'

const MainBanner = () => {
  return (
    <div className='relative'>
      <img src={assets.home_banner_1} alt="banner" className='w-full hidden md:block'/>
      <img src={assets.home_banner_3} alt="banner" className='w-full md:hidden'/>
    </div>
  )
}

export default MainBanner
