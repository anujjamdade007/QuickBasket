import {ShoppingBasket} from "lucide-react";
import {NavLink} from "react-router-dom";
import React from "react";

const Footer = () => {

    return (
        <div className="px-6 md:px-16 lg:px-24 xl:px-32 mt-24 bg-primary/10">
            <div className="flex flex-col md:flex-row items-start justify-between gap-10 py-10 border-b border-gray-500/30 text-gray-500">
                <div>
                    <NavLink to='/' className="flex items-center gap-1">
                        <ShoppingBasket className="w-8 h-8 text-primary" />
                        <span className="text-primary text-xl font-semibold tracking-wide">Quickbasket</span>
                    </NavLink>
                    <p className="max-w-[410px] mt-6">
                        We deliver fresh groceries and snacks straight to your door. Trusted by thousands, we aim to make your shopping experience simple and affordable.</p>
                </div>

            </div>
            <p className="py-4 text-center text-sm md:text-base text-gray-500/80">
                Copyright {new Date().getFullYear()} © Engineer Talks With Bushan. All Right Reserved.
            </p>
        </div>
    );
};

export default Footer